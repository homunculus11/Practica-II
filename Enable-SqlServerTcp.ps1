$ErrorActionPreference = "Stop"

$instanceId = "MSSQL15.MSSQLSERVER"
$base = "HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\$instanceId\MSSQLServer\SuperSocketNetLib\Tcp"

Set-ItemProperty -Path $base -Name Enabled -Value 1
Set-ItemProperty -Path $base -Name ListenOnAllIPs -Value 1

Get-ChildItem -Path $base |
    Where-Object { $_.PSChildName -like "IP*" } |
    ForEach-Object {
        Set-ItemProperty -Path $_.PSPath -Name Enabled -Value 1
        Set-ItemProperty -Path $_.PSPath -Name TcpDynamicPorts -Value ""
        Set-ItemProperty -Path $_.PSPath -Name TcpPort -Value "1433"
    }

Set-ItemProperty -Path "$base\IPAll" -Name TcpDynamicPorts -Value ""
Set-ItemProperty -Path "$base\IPAll" -Name TcpPort -Value "1433"

Restart-Service -Name MSSQLSERVER -Force
Start-Sleep -Seconds 8

Write-Host "SQL Server TCP/IP is enabled on port 1433 and MSSQLSERVER was restarted." -ForegroundColor Green
Read-Host "Press Enter to close"
