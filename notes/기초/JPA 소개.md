## SQL 중심적인 개발의 문제점
- 반복적이고 지루한 코드를 작성한다.
- 모든 객체에 대한 CRUD 코드를 작성한다.
- 자바 객체를 SQL로, SQL를 자바 객체로 변환한다.
```java
public class Member {
    private String memberId;
    private String name; 
    
    ...
}
```

```mysql
INSERT INTO MEMBER(MEMBER_ID, NAME) VALUES  ...
SELECT MEMBER_ID, NAME FROM MEMBER M
UPADTE MEMBER SET ...
```

- 이때 기획 변경으로 필드가 추가된다면?
- 관련된 모든 SQL문을 하나 하나 변경해주어야 한다.
- **즉, SQL에 의존적인 개발을 해야한다.**
- 개발자 = SQL 매퍼

## 패러다임의 불일치
객체 VS 관계형 데이터베이스
- '객체 지향 프로그래밍은 추상화, 캡슐화, 정보은닉, 상속, 다형성 등 시스템의 복잡성을 제어할 수 있는 다양한 장치들을 제공한다.'

### 객체와 관계형 데이터베이스의 차이
1. [상속](#상속)
2. [연관 관계](#연관-관계)
3. 데이터 타입
4. 데이터 식별 방법

#### 상속
##### 객체 상속 관계
![객체의 상속 관계.png](..%2Fimgs%2Fch01%7E02%2F%EA%B0%9D%EC%B2%B4%EC%9D%98%20%EC%83%81%EC%86%8D%20%EA%B4%80%EA%B3%84.png)
##### Table 슈퍼 타입 서브 타입 관계
![DB 슈퍼 타입 서브 타입 관계.png](..%2Fimgs%2Fch01%7E02%2FDB%20%EC%8A%88%ED%8D%BC%20%ED%83%80%EC%9E%85%20%EC%84%9C%EB%B8%8C%20%ED%83%80%EC%9E%85%20%EA%B4%80%EA%B3%84.png)

##### Album 저장
1. 객체 분해하기
2. INSERT INTO ITEM ...
3. INSERT INTO ALBUM ...
#
##### Album 조회
1. 각각의 테이블에 따른 조인 SQL 작성
2. 각각의 객체 생성
3. ... 복잡한 과정 들 ...
4. 따라서 **DB에 저장할 객체에는 상속 관계를 안쓴다.**
##### 자바 컬렉션에 저장하면?
`list.add(album);`
`Album album = list.get(albumId);`

부모 타입으로 조회 후 다형성 활용
`Item item = list.get(albumId);`

#### 연관 관계
- 객체는 참조를 사용한다. member.getTeam();
- 테이블은 외래 키를 사용한다. JOIN ON M.TEAM_ID = T.TEAM_ID
![객체와 DB의 연관관계.png](..%2Fimgs%2Fch01%7E02%2F%EA%B0%9D%EC%B2%B4%EC%99%80%20DB%EC%9D%98%20%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84.png)  
- 객체를 테이블에 맞추어 모델링
```java
class Member {
    String id;          // MEMBER_ID 컬럼 사용
    Long teamId;        // TEAM_ID   FK 사용
    String username;    // USERNAME  컬럼 사용
}
```

```java
class Team {
    Long id;            // TEAM_ID PK 사용
    String name;        // NAME 컬럼 사용
}
```
- 테이블에 맞춘 객체 저장
```sql
INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES ...
```

##### 객체다운 모델링
```java
class Member {
    String id;          // MEMBER_ID 컬럼 사용
    Team team;          // 참조로 연관관계를 맺는다.
    String username;    // USERNAME 컬럼 사용
}
```

```java
class Team {
    Long id;            // TEAM_ID PK 사용
    String name;        // NAME 컬럼 사용
}
```
```sql
INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES ...
```

##### 객체 모델링 조회
```SQL
SELECT  M.*, T.*
FROM    MEMBER M
JOIN    TEAM T ON M.TEAM_ID = T.TEAM_ID
```

```JAVA
public Member find(String memberId){
    // SQL 실행
    Member member = new Member();
    
    // 데이터베이스에서 조회한 회원 관계 정보를 모두 입력
    Team team = new Team();
    // 데이터베이스에서 조회한 팀 관련 정보를 모두 입력
    
    // 회원과 팀 관계 설정
    member.setTeam(team);
    return member;
}
```

##### 객체 모델링, 자바 컬렉션에 관리
`list.add(member);`  
`Member member = list.get(memberId);`  
`Team team = member.getTeam();`

##### 객체 그래프 탐색
객체는 자유롭게 객체 그래프를 탐색할 수 있어야 한다.
![객체 그래프 탐색.png](..%2Fimgs%2Fch01%7E02%2F%EA%B0%9D%EC%B2%B4%20%EA%B7%B8%EB%9E%98%ED%94%84%20%ED%83%90%EC%83%89.png)  

##### 처음 실행하는 SQL에 따라 탐색 범위 결정
```SQL
SELECT  M.*, T.*
FROM    MEMBER M
JOIN    TEAM T ON M.TEAM_ID = T.TEAM_ID
```
```JAVA
member.getTeam();   // OK
member.getOrder();  // null
```

##### 엔티티 신뢰 문제
```JAVA
class MemberService {
    ...
    
    public void process() {
        Member member = memberDAO.find(memberId);
        member.getTeam(); // ??
        member.getOrder().getDelivery(); // ?? 
    }
}
```

##### 모든 객체를 미리 로딩할 수는 없다.
상황에 따라 동일한 회원 조회 메서드를 여러번 생성
```JAVA
memberDAO.getMember(); // Member 만 조회
memberDAO.getMemberWithTeam(); // Member와 Team 조회
memberDAO.getMemberWithOrderWithDelivery(); // Member, Team, Delivery 조회
```

**계층형 아키텍처, 진정한 의미의 계층 분할이 어렵다.**
##### 비교하기
```JAVA
String memberId = "100";
Member member1 = memberDAO.getMember(memberId);
Member member2 = memberDAO.getMember(memberId);

member1 == member2 // 다르다.
```

```java
class MemberDAO {
    public Member getMember(String memberId) {
        String sql = "SELECT * FROM MEMBER MEMBER_ID = ?";
        ...
        
        return new Member(...);
    }
}
```

```java
String memberId = "100";
Member member1 = list.get(memberId);
Member member2 = list.get(memberId);

member1 == member2; //같다.
```
**객체 답게 모델링할 수록 매핑 작업만 늘어난다.**  
**객체를 자바 컬렉션에 저장하듯이 DB에 저장할 수는 없을까?**




## JPA
- Java Persistence API
- 자바 진영의 ORM 기술 표준

### ORM?
- Object-Relational Mapping(객체 관계 매핑)
- 객체는 객체대로 설계
- 관계형 데이터베이스는 관계형 데이터베이스대로 설계
- ORM 프레임워크가 중간에서 매핑
- 대중적인 언어에는 대부분 ORM 기술이 존재한다.
- JPA는 애플리케이션과 JDBC 사이에서 동작한다.
    ![JPA와 JDBC.png](..%2Fimgs%2Fch01%7E02%2FJPA%EC%99%80%20JDBC.png)  
### JPA 동작
1. 저장
    ![JPA 저장.png](..%2Fimgs%2Fch01%7E02%2FJPA%20%EC%A0%80%EC%9E%A5.png)
2. 조회
    ![JPA 조회.png](..%2Fimgs%2Fch01%7E02%2FJPA%20%EC%A1%B0%ED%9A%8C.png)

### JPA 소개
![JPA 소개.png](..%2Fimgs%2Fch01%7E02%2FJPA%20%EC%86%8C%EA%B0%9C.png)

- JPA는 인터페이스의 모음
- JPA 2.1 표준 명세를 구현한 3가지 구현체
  - 하이버네이트, EclipseLink, DataNucleus
        ![JPA 구현체.png](..%2Fimgs%2Fch01%7E02%2FJPA%20%EA%B5%AC%ED%98%84%EC%B2%B4.png)

### JPA를 왜 사용해야 하는가?
- SQL 중심적인 개발에서 객체 중심으로 개발할 수 있게된다.
- 생산성
  - 저장: jpa.persist(member);
  - 조회: Member member = jpa.find(memberId)
  - 수정: member.setName("변경할 이름")
  - 삭제: jpa.remove(member)
- 유지보수
  - 기존: 필드 변경 시 모든 SQL을 수정해야 했다.
  - JPA: 필드만 추가하면된다. SQL은 JPA가 처리하기 때문.
- 패러다임의 불일치 해결
  - JPA와 상속
    - 개발자가 할일: `jpa.persist(album);`
    - 나머진 JPA가 처리: `INSERT INTO ...`
  - JPA와 연관관계, JPA와 객체 그래프 탐색
    - 연관관계 저장: `member.setTeam(team);`, `jpa.persist(member);
    - 객체 그래프 탐색: `Member member = jpa.find(Member.class, memberId);`, `Team team = member.getTean();`
  - JPA와 비교하기
        ```java
        String memberId = "100";
        Member member1 = jpa.find(Member.class, memberId);
        Member member2 = jpa.find(Member.class, memberId);
        member1 == member2 // 같다.
        ```
    - 동일한 트랜잭션에서 조회한 엔티티는 같음을 보장한다.
- 성능
  - 1차 캐시와 동일성(identity) 보장
    - 같은 트랜잭션 안에서는 같은 엔티티를 반환한다. - 약간의 조회 성능 향상
    - DB Isolation Level이 Read Commit 이어도 애플리케이션에서 Repeatable Read 보장
        ```java
        String memberId = "100";
        Member member1 = jpa.find(Member.class, memberId);
        Member member2 = jpa.find(Member.class, memberId);
        // 여기에서 SQL 문은 1 번만 실행된다.
        ```
  - 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
    - 트랜잭션을 커밋할 때 까지 INSERT SQL을 모은다.
    - JDBC BATCH SQL 기능을 사용해서 한 번에 SQL을 전송한다.
        ```java
        transaction.begin(); // [트랜잭션] 시작
        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        //여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
        //커밋하는 순간 데이터베이스에 INSERT SQL을 모아서 보낸다.
        transaction.commit(); // [트랜잭션] 커밋
        ```
    - UPDATE, DELETE로 인한 로우(ROW)락 시간 최소화
    - 트랜잭션 커밋 시 UPDATE, DELETE SQL을 실행하고, 바로 커밋한다.
        ```java
          transaction.begin(); // [트랜잭션] 시작
          changeMember(memberA);
          deleteMember(memberB);
          비즈니스_로직_수행(); //비즈니스 로직 수행 동안 DB 로우 락이 걸리지 않는다.
          //커밋하는 순간 데이터베이스에 UPDATE, DELETE SQL을 보낸다.
          transaction.commit(); // [트랜잭션] 커밋
        ```
  - 지연 로딩(Lazy Loading)
    - 지연 로딩: 객체가 실제 사용될 때 로딩
    - 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회
        ![지연 로딩과 즉시 로딩.png](..%2Fimgs%2Fch01%7E02%2F%EC%A7%80%EC%97%B0%20%EB%A1%9C%EB%94%A9%EA%B3%BC%20%EC%A6%89%EC%8B%9C%20%EB%A1%9C%EB%94%A9.png)  
- 데이터 접근 추상화와 벤더 독립성
- 표준


**ORM은 객체와 RDB 두 기둥위에 있는 기술이다.**






## 데이터베이스 방언
- JPA는 특정 데이터베이스에 종속되지 않는다.
- 각각의 데이터베이스가 제공하는 SQL문법과 함수는 조금씩 다르다.
  - 가변 문자: MySQL은 VARCHAR, Oracle은 VARCHAR2
  - 문자열을 자르는 함수: SQL 표준은 SUBSTRING(), Oracle은 SUBSTR()
  - 페이징: MySQL은 LIMIT, Oracle은 ORWNUM
- 방언: SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능
    ![데이터베이스 방언.png](..%2Fimgs%2Fch01%7E02%2F%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4%20%EB%B0%A9%EC%96%B8.png)

### JPA 구동 방식
![JPA 구동 방식.png](..%2Fimgs%2Fch01%7E02%2FJPA%20%EA%B5%AC%EB%8F%99%20%EB%B0%A9%EC%8B%9D.png)






















