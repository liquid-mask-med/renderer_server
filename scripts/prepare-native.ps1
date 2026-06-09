$ErrorActionPreference = "Stop"

$workspace = Resolve-Path "$PSScriptRoot\..\..\.."
$nativeDir = Join-Path $PSScriptRoot "..\native"
$shaderDir = Join-Path $nativeDir "shaders"
$serverShaderDir = Join-Path $PSScriptRoot "..\shaders"

New-Item -ItemType Directory -Path $nativeDir -Force | Out-Null
New-Item -ItemType Directory -Path $shaderDir -Force | Out-Null
New-Item -ItemType Directory -Path $serverShaderDir -Force | Out-Null

Copy-Item "$workspace\project\renderer_jni\x64\Debug\renderer_jni.dll" $nativeDir -Force
Copy-Item "$workspace\project\renderer_opengl\x64\Debug\renderer_opengl.dll" $nativeDir -Force
Copy-Item "$workspace\project\renderer_vulkan\x64\Debug\renderer_vulkan.dll" $nativeDir -Force
Copy-Item "$workspace\project\renderer_opengl\renderer_opengl\shaders\*" $shaderDir -Force
Copy-Item "$workspace\project\renderer_vulkan\renderer_vulkan\shaders\*.spv" $shaderDir -Force

# The current OpenGL backend resolves shaders from the process working directory.
Copy-Item "$workspace\project\renderer_opengl\renderer_opengl\shaders\*" $serverShaderDir -Force

Write-Host "Native renderer runtime prepared at $nativeDir"
