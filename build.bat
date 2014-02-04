@echo off
cd %~dp0\..

rm -R forge/mcp/src/minecraft/trks
rm -R forge/mcp/src/minecraft/buildcraft
rm -R forge/mcp/src/minecraft/calclavia
rm -R forge/mcp/src/minecraft/cofh
rm -R forge/mcp/src/minecraft/dan200
rm -R forge/mcp/src/minecraft/ic2
rm -R forge/mcp/src/minecraft/mekanism
rm -R forge/mcp/src/minecraft/micdoodle8
rm -R forge/mcp/src/minecraft/powercrystals
rm -R forge/mcp/src/minecraft/rebelkeithy
rm -R forge/mcp/src/minecraft/universalelectricity

cp -pR TrksRecipeDoc/common/trks forge/mcp/src/minecraft/
cp -pR Mekanism/common/* forge/mcp/src/minecraft/

pushd forge\mcp

runtime\bin\python\python_mcp runtime\recompile.py
runtime\bin\python\python_mcp runtime\reobfuscate.py --srgnames

popd
pushd forge\mcp\reobf\minecraft

"C:\Program Files\7-Zip\7z.exe" A -tzip trks.zip trks
move trks.zip %APPDATA%\.minecraft\mods\

:end
popd

rm -R forge/mcp/src/minecraft/trks
rm -R forge/mcp/src/minecraft/buildcraft
rm -R forge/mcp/src/minecraft/calclavia
rm -R forge/mcp/src/minecraft/cofh
rm -R forge/mcp/src/minecraft/dan200
rm -R forge/mcp/src/minecraft/ic2
rm -R forge/mcp/src/minecraft/mekanism
rm -R forge/mcp/src/minecraft/micdoodle8
rm -R forge/mcp/src/minecraft/powercrystals
rm -R forge/mcp/src/minecraft/rebelkeithy
rm -R forge/mcp/src/minecraft/universalelectricity

cd TrksRecipeDoc

time /T