from __future__ import annotations

import re
from dataclasses import asdict, dataclass
from typing import Iterable

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB


RISK_EXAMPLES = (
    "o preço está alto e talvez não vamos renovar",
    "estamos avaliando trocar de fornecedor",
    "o concorrente entregou uma proposta mais barata",
    "não estou satisfeito com o suporte atual",
)
NEUTRAL_EXAMPLES = (
    "gostamos da plataforma e queremos seguir com o projeto",
    "vamos agendar a implantação para o próximo mês",
    "o time aprovou a demonstração do produto",
    "precisamos revisar os detalhes técnicos antes da decisão",
)
COMPETITORS = ("senior", "sap", "oracle", "totvs")
MONEY_PATTERN = re.compile(r"R\$\s?\d+(?:[\.,]\d+)?\s?(?:mil|k)?", re.IGNORECASE)


@dataclass(frozen=True)
class IntelligenceCard:
    sentiment: str
    churn_risk: str
    upsell_opportunity: str
    budget: str | None
    competitors: tuple[str, ...]
    listening_score: int
    recommendation: str
    evidence: tuple[str, ...]

    def to_dict(self) -> dict:
        return asdict(self)


class ChurnRiskModel:
    """Classificador simples, local e reproduzível para o MVP."""

    def __init__(self) -> None:
        self._vectorizer = TfidfVectorizer(ngram_range=(1, 2), lowercase=True)
        training_texts = [*RISK_EXAMPLES, *NEUTRAL_EXAMPLES]
        labels = ["HIGH", "HIGH", "HIGH", "HIGH", "LOW", "LOW", "LOW", "LOW"]
        matrix = self._vectorizer.fit_transform(training_texts)
        self._model = MultinomialNB().fit(matrix, labels)

    def predict(self, text: str) -> str:
        return self._model.predict(self._vectorizer.transform([text]))[0]


class MeetingAnalyzer:
    def __init__(self, risk_model: ChurnRiskModel | None = None) -> None:
        self._risk_model = risk_model or ChurnRiskModel()

    def analyze(self, transcript: str) -> IntelligenceCard:
        normalized = transcript.lower()
        risk = self._risk_model.predict(transcript)
        evidence = tuple(self._find_evidence(transcript))
        competitors = tuple(name.upper() for name in COMPETITORS if name in normalized)
        budget_match = MONEY_PATTERN.search(transcript)
        budget = budget_match.group(0) if budget_match else None
        score = self._listening_score(transcript)
        sentiment = "NEGATIVE" if risk == "HIGH" else "POSITIVE"
        opportunity = "HIGH" if any(word in normalized for word in ("expandir", "upgrade", "novo módulo")) else "NONE"
        return IntelligenceCard(
            sentiment=sentiment,
            churn_risk=risk,
            upsell_opportunity=opportunity,
            budget=budget,
            competitors=competitors,
            listening_score=score,
            recommendation=self._recommend(risk, opportunity, score),
            evidence=evidence,
        )

    @staticmethod
    def _find_evidence(transcript: str) -> Iterable[str]:
        keywords = ("preço", "renovar", "trocar", "insatisfeito", "expandir", "upgrade", "R$")
        return [line.strip() for line in transcript.splitlines() if any(word in line.lower() for word in keywords)]

    @staticmethod
    def _listening_score(transcript: str) -> int:
        seller = sum(len(line) for line in transcript.splitlines() if line.upper().startswith("VENDEDOR:"))
        client = sum(len(line) for line in transcript.splitlines() if line.upper().startswith("CLIENTE:"))
        total = seller + client
        if not total:
            return 50
        seller_ratio = seller / total
        return max(0, min(100, round((1 - abs(0.45 - seller_ratio) / 0.45) * 100)))

    @staticmethod
    def _recommend(risk: str, opportunity: str, score: int) -> str:
        if risk == "HIGH":
            return "Acionar CS em até 24 horas e tratar a objeção identificada."
        if opportunity == "HIGH":
            return "Agendar proposta de expansão com o decisor."
        if score < 60:
            return "Revisar a condução: o vendedor falou em proporção inadequada."
        return "Registrar o próximo passo e acompanhar a oportunidade."
