# Rode este arquivo no PowerShell após `docker compose up -d --build`.
$body = Get-Content "$PSScriptRoot\..\examples\demo-request.json" -Raw
$result = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/meetings" -ContentType "application/json" -Body $body
$result | ConvertTo-Json -Depth 6
Write-Host "Sessão criada: $($result.session.id)"
Write-Host "Consulta: http://localhost:8080/api/meetings/$($result.session.id)"
$question = Get-Content "$PSScriptRoot\..\examples\demo-question.json" -Raw
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/meetings/$($result.session.id)/questions" -ContentType "application/json" -Body $question | ConvertTo-Json -Depth 6
