from __future__ import annotations

import json
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

from kaivi.analyzer import MeetingAnalyzer
from kaivi.rag import TranscriptRag


def analyze_payload(payload: dict, analyzer: MeetingAnalyzer | None = None) -> tuple[dict, HTTPStatus]:
    transcript = payload.get("transcript")
    if not isinstance(transcript, str) or not transcript.strip():
        return {"message": "transcript is required"}, HTTPStatus.BAD_REQUEST

    card = (analyzer or MeetingAnalyzer()).analyze(transcript)
    return {
        "sentiment": card.sentiment,
        "churnRisk": card.churn_risk,
        "upsellOpportunity": card.upsell_opportunity,
        "budget": card.budget,
        "competitors": list(card.competitors),
        "listeningScore": card.listening_score,
        "recommendation": card.recommendation,
        "evidence": list(card.evidence),
    }, HTTPStatus.OK


def answer_payload(payload: dict, rag: TranscriptRag | None = None) -> tuple[dict, HTTPStatus]:
    transcript = payload.get("transcript")
    question = payload.get("question")
    if not isinstance(transcript, str) or not transcript.strip():
        return {"message": "transcript is required"}, HTTPStatus.BAD_REQUEST
    if not isinstance(question, str) or not question.strip():
        return {"message": "question is required"}, HTTPStatus.BAD_REQUEST

    response = (rag or TranscriptRag()).answer(transcript, question)
    return {
        "answer": response.answer,
        "sources": [source.__dict__ for source in response.sources],
    }, HTTPStatus.OK


class AnalysisRequestHandler(BaseHTTPRequestHandler):
    analyzer = MeetingAnalyzer()
    rag = TranscriptRag()

    def do_GET(self) -> None:
        if self.path == "/health":
            self._write_json({"status": "UP"}, HTTPStatus.OK)
            return
        self._write_json({"message": "not found"}, HTTPStatus.NOT_FOUND)

    def do_POST(self) -> None:
        if self.path not in {"/analyze", "/chat"}:
            self._write_json({"message": "not found"}, HTTPStatus.NOT_FOUND)
            return
        try:
            size = int(self.headers.get("Content-Length", "0"))
            payload = json.loads(self.rfile.read(size) or "{}")
        except (ValueError, json.JSONDecodeError):
            self._write_json({"message": "invalid JSON"}, HTTPStatus.BAD_REQUEST)
            return
        response, status = (
            analyze_payload(payload, self.analyzer)
            if self.path == "/analyze"
            else answer_payload(payload, self.rag)
        )
        self._write_json(response, status)

    def _write_json(self, payload: dict, status: HTTPStatus) -> None:
        encoded = json.dumps(payload).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(encoded)))
        self.end_headers()
        self.wfile.write(encoded)

    def log_message(self, format: str, *args) -> None:
        return


def run(host: str = "0.0.0.0", port: int = 5001) -> None:
    ThreadingHTTPServer((host, port), AnalysisRequestHandler).serve_forever()


if __name__ == "__main__":
    run()
