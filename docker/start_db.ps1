# start_db.ps1 — start DB stack and show created databases
# Usage: Open PowerShell in project root and run: .\docker\start_db.ps1

# Parse .env into a hashtable
$envHash = @{}
Get-Content -Path ".env" | ForEach-Object {
    if ($_ -and -not $_.Trim().StartsWith('#')) {
        $parts = $_ -split '=',2
        if ($parts.Count -eq 2) { $envHash[$parts[0].Trim()] = $parts[1].Trim() }
    }
}

docker-compose up -d
Start-Sleep -Seconds 6

$rootPwd = $envHash['MYSQL_ROOT_PASSWORD']
if (-not $rootPwd) {
    Write-Host "WARN: MYSQL_ROOT_PASSWORD not found in .env — please run docker exec manually with your root password."
    exit 0
}

# Show databases inside container
docker exec -i ibs-db mysql -uroot -p$rootPwd -e "SHOW DATABASES;"
