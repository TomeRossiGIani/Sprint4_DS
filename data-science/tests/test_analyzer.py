import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parents[1] / "src"))

from kaivi.analyzer import MeetingAnalyzer


class MeetingAnalyzerTest(unittest.TestCase):
    def setUp(self):
        self.analyzer = MeetingAnalyzer()

    def test_extracts_risk_budget_competitor_and_evidence(self):
        transcript = (
            "VENDEDOR: Como está a operação?\n"
            "CLIENTE: O preço está alto e talvez não vamos renovar.\n"
            "CLIENTE: A SAP ofereceu algo por R$ 50 mil."
        )
        card = self.analyzer.analyze(transcript)
        self.assertEqual("HIGH", card.churn_risk)
        self.assertEqual("R$ 50 mil", card.budget)
        self.assertIn("SAP", card.competitors)
        self.assertTrue(card.evidence)

    def test_detects_upsell_from_expansion_words(self):
        transcript = "CLIENTE: Gostamos da plataforma e queremos expandir para um novo módulo."
        card = self.analyzer.analyze(transcript)
        self.assertEqual("HIGH", card.upsell_opportunity)

    def test_empty_speaker_labels_has_neutral_listening_score(self):
        card = self.analyzer.analyze("A reunião foi concluída e o próximo passo foi definido.")
        self.assertEqual(50, card.listening_score)


if __name__ == "__main__":
    unittest.main()
