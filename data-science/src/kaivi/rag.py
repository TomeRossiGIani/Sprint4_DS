from __future__ import annotations

from dataclasses import dataclass

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


@dataclass(frozen=True)
class RagSource:
    reference: str
    text: str
    score: float


@dataclass(frozen=True)
class RagAnswer:
    answer: str
    sources: tuple[RagSource, ...]


class TranscriptRag:
    """Recupera trechos relevantes antes de responder sobre uma reunião."""

    def answer(self, transcript: str, question: str) -> RagAnswer:
        chunks = [line.strip() for line in transcript.splitlines() if line.strip()]
        if not chunks or not question.strip():
            return RagAnswer("Não há evidência suficiente na reunião para responder.", ())

        query = self._expand_question(question)
        vectorizer = TfidfVectorizer(ngram_range=(1, 2), stop_words=None)
        matrix = vectorizer.fit_transform(chunks)
        scores = cosine_similarity(vectorizer.transform([query]), matrix).flatten()
        ranked = sorted(enumerate(scores), key=lambda item: item[1], reverse=True)
        selected = [item for item in ranked[:2] if item[1] > 0]
        if not selected:
            return RagAnswer("Não encontrei um trecho da reunião que responda a essa pergunta.", ())

        sources = tuple(
            RagSource(reference=f"Linha {index + 1}", text=chunks[index], score=round(float(score), 3))
            for index, score in selected
        )
        return RagAnswer(answer=f"Com base na reunião: {sources[0].text}", sources=sources)

    @staticmethod
    def _expand_question(question: str) -> str:
        normalized = question.lower()
        expansions = []
        if "obje" in normalized:
            expansions.append("preço alto renovar insatisfeito problema")
        if "orçamento" in normalized or "budget" in normalized:
            expansions.append("R$ valor investimento custo")
        if "concorr" in normalized:
            expansions.append("SAP Oracle Senior TOTVS")
        return f"{question} {' '.join(expansions)}"
