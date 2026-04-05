/**
 * Quiz frontend — vanilla JS + fetch()
 * Endpoints (configure via API_BASE if needed):
 *   GET  /api/questions
 *   POST /api/submit   body: { questionId, selectedOption }
 *   GET  /api/results
 */

// Same host when UI is served from Spring (http://localhost:8080); else talk to API on 8080
const API_BASE =
  window.location.protocol === "http:" || window.location.protocol === "https:"
    ? window.location.origin
    : "http://localhost:8080";

/** @type {{ id: number, question: string, options: string[] }[]} */
let questions = [];

/** Index of the question currently shown (0-based). */
let currentIndex = 0;

// --- DOM refs ---
const el = {
  loadingOverlay: document.getElementById("loading-overlay"),
  errorBanner: document.getElementById("error-banner"),
  screenStart: document.getElementById("screen-start"),
  screenQuiz: document.getElementById("screen-quiz"),
  screenResults: document.getElementById("screen-results"),
  btnStart: document.getElementById("btn-start"),
  btnNext: document.getElementById("btn-next"),
  btnRestart: document.getElementById("btn-restart"),
  progressLabel: document.getElementById("progress-label"),
  progressFill: document.getElementById("progress-fill"),
  progressBar: document.querySelector(".progress-bar"),
  questionText: document.getElementById("question-text"),
  optionsContainer: document.getElementById("options-container"),
  resultsMessage: document.getElementById("results-message"),
  resultsDetail: document.getElementById("results-detail"),
};

// --- Loading / errors ---

function setLoading(on) {
  el.loadingOverlay.classList.toggle("hidden", !on);
  el.loadingOverlay.setAttribute("aria-hidden", on ? "false" : "true");
  el.btnStart.disabled = on;
  el.btnNext.disabled = on || !getSelectedOption();
}

function showError(message) {
  el.errorBanner.textContent = message;
  el.errorBanner.classList.remove("hidden");
}

function hideError() {
  el.errorBanner.classList.add("hidden");
  el.errorBanner.textContent = "";
}

// --- HTTP ---

/**
 * @param {string} path
 * @param {RequestInit} [init]
 */
async function apiFetch(path, init = {}) {
  const url = `${API_BASE}${path}`;
  const headers = { Accept: "application/json", ...init.headers };
  if (init.body && typeof init.body === "string" && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }
  const res = await fetch(url, { ...init, headers });
  const text = await res.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      /* non-JSON body */
    }
  }
  if (!res.ok) {
    const msg =
      (data && (data.message || data.error)) ||
      `Request failed (${res.status} ${res.statusText})`;
    throw new Error(msg);
  }
  return data;
}

async function fetchQuestions() {
  const data = await apiFetch("/api/questions");
  if (!Array.isArray(data)) {
    throw new Error("Invalid response: expected an array of questions.");
  }
  return data;
}

/**
 * @param {number} questionId
 * @param {string} selectedOption
 */
async function submitAnswer(questionId, selectedOption) {
  await apiFetch("/api/submit", {
    method: "POST",
    body: JSON.stringify({ questionId, selectedOption }),
  });
}

async function fetchResults() {
  return apiFetch("/api/results");
}

// --- Screens ---

function showScreen(name) {
  const screens = {
    start: el.screenStart,
    quiz: el.screenQuiz,
    results: el.screenResults,
  };
  Object.values(screens).forEach((s) => {
    s.classList.add("hidden");
    s.classList.remove("visible");
  });
  const active = screens[name];
  active.classList.remove("hidden");
  active.classList.add("visible");
}

// --- Quiz UI ---

function getSelectedOption() {
  const checked = el.optionsContainer.querySelector('input[name="answer"]:checked');
  return checked ? checked.value : "";
}

function updateProgress() {
  const total = questions.length;
  const n = currentIndex + 1;
  el.progressLabel.textContent = `Question ${n} of ${total}`;
  const pct = total > 0 ? Math.round((n / total) * 100) : 0;
  el.progressFill.style.width = `${pct}%`;
  if (el.progressBar) {
    el.progressBar.setAttribute("aria-valuenow", String(pct));
    el.progressBar.setAttribute("aria-valuemax", "100");
  }
}

function renderCurrentQuestion() {
  const q = questions[currentIndex];
  if (!q) return;

  el.questionText.textContent = q.question || "";
  el.optionsContainer.innerHTML = "";

  const opts = Array.isArray(q.options) ? q.options : [];
  opts.forEach((opt, i) => {
    const id = `opt-${q.id}-${i}`;
    const label = document.createElement("label");
    label.className = "option-label";
    label.htmlFor = id;

    const input = document.createElement("input");
    input.type = "radio";
    input.name = "answer";
    input.id = id;
    input.value = opt;

    const span = document.createElement("span");
    span.className = "option-text";
    span.textContent = opt;

    label.appendChild(input);
    label.appendChild(span);
    el.optionsContainer.appendChild(label);
  });

  el.btnNext.disabled = !getSelectedOption();
  updateProgress();
}

/**
 * Normalize results payload from backend (flexible keys).
 * @param {*} data
 */
function formatResultsSummary(data) {
  if (data == null) return "No details returned.";
  if (typeof data === "number") return `Score: ${data}`;

  const score =
    data.score ??
    data.correct ??
    data.correctCount ??
    data.points ??
    data.totalScore;
  const total =
    data.total ??
    data.totalQuestions ??
    data.totalCount ??
    data.maxScore ??
    data.outOf;

  if (score != null && total != null) {
    return `You scored ${score} out of ${total}.`;
  }
  if (score != null) {
    return `Your score: ${score}`;
  }

  try {
    return JSON.stringify(data, null, 2);
  } catch {
    return String(data);
  }
}

function showResultsScreen(resultsData) {
  el.resultsMessage.textContent = "Quiz complete!";
  el.resultsDetail.textContent = formatResultsSummary(resultsData);
  showScreen("results");
}

// --- Flow ---

async function startQuiz() {
  hideError();
  setLoading(true);
  try {
    questions = await fetchQuestions();
    if (questions.length === 0) {
      showError("No questions available.");
      return;
    }
    currentIndex = 0;
    showScreen("quiz");
    renderCurrentQuestion();
  } catch (e) {
    showError(e instanceof Error ? e.message : "Failed to load questions.");
  } finally {
    setLoading(false);
  }
}

async function onNextClick() {
  const q = questions[currentIndex];
  const selected = getSelectedOption();
  if (!q || !selected) return;

  hideError();
  setLoading(true);
  try {
    await submitAnswer(q.id, selected);

    const isLast = currentIndex >= questions.length - 1;
    if (isLast) {
      const results = await fetchResults();
      showResultsScreen(results);
    } else {
      currentIndex += 1;
      renderCurrentQuestion();
    }
  } catch (e) {
    showError(e instanceof Error ? e.message : "Submit or results request failed.");
  } finally {
    setLoading(false);
  }
}

function restart() {
  hideError();
  questions = [];
  currentIndex = 0;
  el.optionsContainer.innerHTML = "";
  showScreen("start");
}

// --- Init ---

function init() {
  el.btnStart.addEventListener("click", startQuiz);
  el.btnNext.addEventListener("click", onNextClick);
  el.btnRestart.addEventListener("click", restart);
  // One listener for dynamically built option radios
  el.optionsContainer.addEventListener("change", (e) => {
    if (e.target.matches('input[name="answer"]')) {
      el.btnNext.disabled = !getSelectedOption();
    }
  });
}

init();
