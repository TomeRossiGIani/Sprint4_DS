import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parents[1] / "src"))

from http import HTTPStatus

from kaivi.api import analyze_payload, answer_payload


class AnalysisApiTest(unittest.TestCase):
    def test_returns_intelligence_card(self):
        response, status = analyze_payload({"transcript": "CLIENTE: O preço está alto e talvez não vamos renovar."})
        self.assertEqual(HTTPStatus.OK, status)
        self.assertEqual("HIGH", response["churnRisk"])

    def test_rejects_empty_transcript(self):
        _, status = analyze_payload({"transcript": ""})
        self.assertEqual(HTTPStatus.BAD_REQUEST, status)

    def test_returns_answer_with_transcript_source(self):
        response, status = answer_payload({
            "question": "Qual foi a objeção?",
            "transcript": "CLIENTE: O preço está alto.\nVENDEDOR: Vamos avaliar uma condição comercial.",
        })
        self.assertEqual(HTTPStatus.OK, status)
        self.assertIn("preço", response["answer"])
        self.assertEqual("Linha 1", response["sources"][0]["reference"])


if __name__ == "__main__":
    unittest.main()
