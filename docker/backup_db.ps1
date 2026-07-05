<#
Simple PowerShell script to backup the `ibs_core` database from the MySQL docker container.
Usage: Run from project root: .\docker\backup_db.ps1 -OutDir .\backups
#>

param(
    [string]$OutDir = ".\backups"
)

if (-not (Test-Path $OutDir)) {
    New-Item -ItemType Directory -Path $OutDir | Out-Null
}

# Read .env if present
$envFile = Join-Path -Path (Get-Location) -ChildPath ".env"
if (Test-Path $envFile) {
    $lines = Get-Content $envFile | Where-Object { $_ -and -not ($_.Trim().StartsWith('#')) }
    foreach ($l in $lines) {
        $parts = $l -split '=',2
        if ($parts.Count -eq 2) {
            $k = $parts[0].Trim()
            $v = $parts[1].Trim()
            if ($k -eq 'MYSQL_ROOT_PASSWORD') { $rootPwd = $v }
        }
    }
}

if (-not $rootPwd) {
    Write-Host "MYSQL_ROOT_PASSWORD not found in .env. Please set environment variable or edit this script." -ForegroundColor Yellow
    exit 1
}

$timestamp = (Get-Date).ToString('yyyyMMdd_HHmmss')
$outFile = Join-Path $OutDir "ibs_core_dump_$timestamp.sql"

Write-Host "Creating backup to $outFile ..."
docker exec -i ibs-db mysqldump -uroot -p$rootPwd ibs_core > $outFile

if ($LASTEXITCODE -eq 0) { Write-Host "Backup saved: $outFile" -ForegroundColor Green } else { Write-Host "Backup failed" -ForegroundColor Red }

