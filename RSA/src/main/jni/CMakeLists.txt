set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../libs/${ANDROID_ABI})
add_library(
        RSALib SHARED RSAUtils.cpp
)
target_link_libraries(
        RSALib
        log )