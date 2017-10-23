@echo off
set GRID_HOST=172.16.17.57
set GRID_PORT=8888
cd grid
curl -H "Content-Type: application/json" -X POST --data @selendroid-nodes-config.json http://%GRID_HOST%:%GRID_PORT%/grid/register
cd ..