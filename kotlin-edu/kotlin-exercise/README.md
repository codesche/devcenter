# 📚 Kotlin Study - Basic Grammar

## 🚀 프로젝트 소개

이 프로젝트는 Kotlin 언어의 기본 문법과 Spring Boot + Kotlin 프로젝트
세팅 과정을 학습하기 위한 저장소입니다.
Java 개발 경험자를 기준으로 Kotlin의 특징을 빠르게 익히고, 실습 예제를
통해 실무 감각을 익히는 것을 목표로 합니다.

------------------------------------------------------------------------

## ⚙️ 개발 환경

-   **언어**: Kotlin (JVM 기반)
-   **빌드 도구**: Gradle - Kotlin DSL(`build.gradle.kts`) 권장
-   **JDK 버전**: Java 17 (LTS, Spring Boot 3.x 이상 필수)
-   **IDE**: IntelliJ IDEA (Ultimate 또는 Community)

------------------------------------------------------------------------

## 🛠️ 프로젝트 설정

1.  **Spring Boot Initializr** 설정
    -   Language: Kotlin
    -   Type: Gradle - Kotlin DSL
    -   Group: `org.com`
    -   Artifact: `kotlin-edu`
    -   Package: `org.com.kotlinedu`
    -   Java Version: 17
2.  **JDK 설정**
    -   JDK 17 (Eclipse Temurin, Amazon Corretto, Homebrew OpenJDK 모두
        가능)
    -   Gradle JVM도 동일하게 17로 설정

------------------------------------------------------------------------

## 📖 Kotlin 기본 문법

### 🔹 1. 기본 문법

-   Hello World
-   변수(`val`/`var`)
-   문자열 템플릿
-   조건문(`if`)
-   `when` (switch 대체)
-   반복문(`for`, `while`)
-   함수 정의 / 기본값 매개변수 / Named Arguments

### 🔹 2. 객체지향

-   클래스 & 생성자
-   `data class`
-   상속 (`open` / `override`)
-   인터페이스
-   `object` (싱글톤)
-   `companion object`
-   `enum class`, `sealed class`
-   inner class

### 🔹 3. 코틀린다운 문법

-   Null Safety (`?`, `?.`, `?:`)
-   스마트 캐스트
-   확장 함수
-   람다 & 고차 함수
-   컬렉션 처리 (`map`, `filter`)
-   `mutableList`, `set`, `map`
-   구조 분해 할당

------------------------------------------------------------------------

## 📌 학습 진행 상황

-   ✅ Kotlin 기본 문법 30선 정리 완료
-   ✅ JDK 17 설정
-   ✅ Gradle - Kotlin DSL 설정
-   ⏳ 다음 단계: **코틀린 고급 문법 (코루틴, 제네릭, DSL)** 및 **Spring
    Boot + Kotlin 실습**

------------------------------------------------------------------------

## 📂 프로젝트 구조

``` bash
kotlin-edu/
 ├── src/
 │   └── main/
 │       └── kotlin/
 │           └── org/com/kotlinedu/
 │               └── Main.kt   # 30가지 Kotlin 예제 모음
 └── build.gradle.kts          # Gradle Kotlin DSL
 └── README.md
```

------------------------------------------------------------------------

## 🚀 다음 학습 로드맵

1.  Kotlin 고급 문법
    -   코루틴
    -   제네릭
    -   함수형 프로그래밍 패턴
2.  Spring Boot + Kotlin
    -   REST API 만들기
    -   JPA 연동
    -   Gradle 빌드 스크립트 최적화
