# Kaivi.AI — Sprint 4

MVP de inteligência conversacional para transformar transcrições de reuniões em decisões rastreáveis.

## O que funciona nesta entrega

1. Uma transcrição é enviada à API Java (`POST /api/meetings`).
2. O caso de uso delega a análise textual ao serviço de Data Science via HTTP.
3. O resultado contém sentimento, risco de churn, oportunidade de upsell, orçamento, concorrentes, score de escuta e recomendação.
4. A sessão e os insights são persistidos pelo repositório e podem ser recuperados com `GET /api/meetings/{id}`.
5. O Chat RAG recupera os trechos mais relevantes antes de responder a `POST /api/meetings/{id}/questions`.

O modelo de demonstração é intencionalmente local e explicável: TF-IDF + Naive Bayes classifica risco de churn; regras determinísticas extraem valores, concorrentes e evidências. Isso evita prometer uma IA de caixa-preta no MVP.

## Estrutura

| Pasta | Responsabilidade |
|---|---|
| `backend` | API REST Java/Spring Boot organizada em API, aplicação, domínio e infraestrutura. |
| `data-science` | Pipeline Python, modelo, API HTTP local e testes automatizados. |
| `db` | Um único arquivo SQL com DDL, DML e DQL. |
| `examples` | Transcrição sintética para a demonstração. |
| `scripts` | Demonstração pronta em PowerShell e Bash. |

## Executar os testes do pipeline

```bash
cd data-science
python -m unittest discover -s tests -v
```

## Demonstrar o MVP

Pré-requisito: Docker Desktop aberto.

```bash
docker compose up -d --build
```

No Windows PowerShell, rode:

```powershell
.\scripts\demo.ps1
```

O comando cria uma sessão usando `examples/demo-request.json`, retorna o cartão com churn alto, budget, SAP, evidências e recomendação, e em seguida pergunta a objeção principal pelo Chat RAG.

```text
http://localhost:8080/api/meetings/{session.id}
```

Para parar tudo depois da apresentação:

```bash
docker compose down
```

## Princípios de código limpo aplicados

- O domínio não conhece HTTP, banco ou bibliotecas de IA.
- O controlador só valida/transforma a requisição; a regra fica no caso de uso.
- `TranscriptAnalysisClient` é uma porta: a implementação Python/HTTP pode mudar sem alterar o domínio.
- Classes têm nomes de intenção e métodos pequenos; não há classe `Utils` genérica.
- Cada insight guarda a evidência textual que explica o resultado.
