@REM 这个文件会在 GitHub Actions 的 Windows runner 上执行

@REM 直接用 vcpkg install boost 也可以跑, 但是这会在 CI 上装一小时, 所以就只装了最少的能过编译的包

if "%PROCESSOR_ARCHITECTURE%"=="ARM64" (
  set VCPKG_TRIPLET=arm64-windows
) else (
  set VCPKG_TRIPLET=x64-windows
)

vcpkg install openssl:%VCPKG_TRIPLET% boost-variant:%VCPKG_TRIPLET% boost-system:%VCPKG_TRIPLET% boost-range:%VCPKG_TRIPLET% boost-crc:%VCPKG_TRIPLET% boost-logic:%VCPKG_TRIPLET% boost-parameter:%VCPKG_TRIPLET% boost-asio:%VCPKG_TRIPLET% boost-variant2:%VCPKG_TRIPLET% boost-multi-index:%VCPKG_TRIPLET% boost-multiprecision:%VCPKG_TRIPLET%
@REM vcpkg install openssl:%VCPKG_TRIPLET% boost:%VCPKG_TRIPLET%

choco install swig -y
choco install openssl -y

vcpkg integrate install
