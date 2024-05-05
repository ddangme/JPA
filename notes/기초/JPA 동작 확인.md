## 생성하기 매핑하기
- `@Entity` : JPA가 관리할 객체
- `@Id`: 데이터베이스 PK와 매핑
```java
@Entity
public class Member {
    
    @Id
    private Long id;
    private String name;
}
```

```sql
CREATE TABLE Member(
    ID BIGINT NOT NULL,
    NAME VARCHAR(255),
    PRIMARY KEY (ID)
);
```

> 🚨  
> 1. `EntityManagerFactory`는 하나만 생성해서 애플리케이션 전체에서 공유한다.
> 2. `EntityManagerFactory`는 쓰레드 간에 공유하지 않는다. (사용하고 버려야한다.)
> 3. JPA의 모든 데이터 변경은 트랜잭션 안에서 실행한다.

### JPQL 소개
- 가장 단순한 조회 방법
  - EntityManager.find()
  - 객체 그래프 탐색(a.getB().getC())
- 나이가 18상 이상인 회원을 모두 검색하고 싶다면?
- JPA를 사용하면 엔티티 객체를 중심으로 개발할 수 있다.
- 문제는 검색 쿼리
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색한다.
- 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능하다.
- 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요하다.
- SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어를 제공한다.
- SQL과 문법이 유사하다. SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 을 지원한다.
- JPQL은 엔티티 객체를 대상으로 쿼리한다.
- SQL은 데이터베이스 테이블을 대상으로 쿼리한다.
- 즉, 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리이다.
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.
- JPQL을 한마디로 정의하면 객체 지향 SQLdlek.
- 
### JPQL 실습
- 전체 회원 검색
- ID가 2 이상인 회원만 검색
- 이름이 같은 회원만 검색
- 자세한 내용은 객체지향 쿼리에서 학습한다.

## 영속성 컨텍스트

### JPA에서 가장 중요한 2가지
- 객체와 관계형 데이터베이스 매핑하기(Object Relational Mapping)
- 영속성 컨텍스트

![엔티티 매니저 팩토리와 엔티티 매니저.png](..%2Fimgs%2Fch01%7E02%2F%EC%97%94%ED%8B%B0%ED%8B%B0%20%EB%A7%A4%EB%8B%88%EC%A0%80%20%ED%8C%A9%ED%86%A0%EB%A6%AC%EC%99%80%20%EC%97%94%ED%8B%B0%ED%8B%B0%20%EB%A7%A4%EB%8B%88%EC%A0%80.png)

### 영속성 컨텍스트
- JPA를 이해하는데 가장 중요한 용어
- "엔티티를 영구 저장하는 환경"이라는 뜻
- EntityManager.persist(entity);

### 엔티티 매니저? 영속성 컨텍스트?
- 영속성 컨텍스트는 논리적인 개념이다.
- 눈에 보이지 않는다.
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근한다.

![J2SE.png](..%2Fimgs%2Fch01%7E02%2FJ2SE.png)

### 엔티티의 생명주기
- 비영속(new/transient)
  - 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
- 영속(managed)
  - 영속성 컨텍스트에 관리되는 상태
- 준영속(detached)
  - 영속성 컨텍스트에 저장되었다가 분리된 상태
- 삭제(removed)
  - 삭제된 상태

![엔티티의 생명주기.png](..%2Fimgs%2Fch01%7E02%2F%EC%97%94%ED%8B%B0%ED%8B%B0%EC%9D%98%20%EC%83%9D%EB%AA%85%EC%A3%BC%EA%B8%B0.png)

#### 비영속
![비영속.png](..%2Fimgs%2Fch01%7E02%2F%EB%B9%84%EC%98%81%EC%86%8D.png)
```java
//객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");
```

#### 영속
![영속.png](..%2Fimgs%2Fch01%7E02%2F%EC%98%81%EC%86%8D.png)
```java
//객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername(“회원1”);

EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

//객체를 저장한 상태(영속)
em.persist(member);
```

#### 준영속
```java
//회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태
em.detach(member);
```

#### 삭제
```java
//객체를 삭제한 상태(삭제)
em.remove(member);
```


#### 영속성 컨텍스트의 이점
- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연
- 변경 감지(dirty Checking)
- 지연 로딩(Lazy Loading)

### 흐름
엔티티 조회, 1차 캐시  
![엔티티 조회 1차 캐시.png](..%2Fimgs%2Fch01%7E02%2F%EC%97%94%ED%8B%B0%ED%8B%B0%20%EC%A1%B0%ED%9A%8C%201%EC%B0%A8%20%EC%BA%90%EC%8B%9C.png)    
```java
//엔티티를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");
//엔티티를 영속
em.persist(member);
```

1차 캐시에서 조회    
![1차 캐시 조회.png](..%2Fimgs%2Fch01%7E02%2F1%EC%B0%A8%20%EC%BA%90%EC%8B%9C%20%EC%A1%B0%ED%9A%8C.png)  
```java
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");

//1차 캐시에 저장됨
em.persist(member);

//1차 캐시에서 조회
Member findMember = em.find(Member.class, "member1");
```

데이터베이스에서 조회   
![데이터베이스에서 조회.png](..%2Fimgs%2Fch01%7E02%2F%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4%EC%97%90%EC%84%9C%20%EC%A1%B0%ED%9A%8C.png)

```java
// 데이터베이스에서 조회   
member findMember2 = em.find(Member.class, "member2");
```

### 영속 엔티티의 동일성 보장
```java
Member a = em.find(Member.class, "member1");
Member b = em.find(Member.class, "member1");

System.out.println(a == b); //동일성 비교 true
```
1차 캐시로 반복 가능한 읽기 (REPEATABLE READ) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공한다.


### 엔티티 등록
트랜잭션을 지원하는 쓰기 지연
```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();

//엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
transaction.begin(); // [트랜잭션] 시작

em.persist(memberA);
em.persist(memberB);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.

//커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
transaction.commit(); // [트랜잭션] 커밋
```
![저장 A.png](..%2Fimgs%2Fch01%7E02%2F%EC%A0%80%EC%9E%A5%20A.png)

![저장 B.png](..%2Fimgs%2Fch01%7E02%2F%EC%A0%80%EC%9E%A5%20B.png)

![commit.png](..%2Fimgs%2Fch01%7E02%2Fcommit.png)



### 엔티티 수정
변경 감지
```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
transaction.begin(); // [트랜잭션] 시작

// 영속 엔티티 조회
Member memberA = em.find(Member.class, "memberA");

// 영속 엔티티 데이터 수정
memberA.setUsername("hi");
memberA.setAge(10);

//em.update(member) 이런 코드가 있어야 하지 않을까?
transaction.commit(); // [트랜잭션] 커밋
```
![변경 감지.png](..%2Fimgs%2Fch01%7E02%2F%EB%B3%80%EA%B2%BD%20%EA%B0%90%EC%A7%80.png)

### 엔티티 삭제
```java
//삭제 대상 엔티티 조회
Member memberA = em.find(Member.class, “memberA");
        
em.remove(memberA);//엔티티 삭제
```
### 플러시
영속성 컨텍스트의 변경내용을 데이터베이스에 반영

### 플러시 발생
- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송(등록, 수정, 삭제 쿼리)

### 영속성 컨텍스트를 플러시하는 방법
| 방법         | 플러시 호출 여부 |
|------------|-----------|
| em.flush() | 직접 호출     |
| 트랜잭션 커밋    | 플러시 자동 호출 |
| JPQL 쿼리 실행 | 플러시 자동 호출 |

### JPQL 쿼리 실행 시 플러시가 자동으로 호출되는 이유
```java
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);

// 중간에 JPQL 실행
query = em.createQuery("select m from Member m", Member.class);
List<Member> members= query.getResultList();
```
### 플러시 모드 옵션
```java
em.setFlushMode(FlushModeType.COMMIT)
```
| 옵션                   | 설명                       |
|----------------------|--------------------------|
| FlushModeType.AUTO   | 커밋이나 쿼리를 실행할 때 플러시 (기본값) |
| FlushModeType.COMMIT | 커밋할 때만 플러시               |

### 플러시
- 영속성 컨텍스트를 비우지 않는다.
- 영속성 컨텍스트의 변경 내용을 데이터베이스에 동기화한다.
- 트랜잭션이라는 작업 단위가 중요하다. 커밋 직전에만 동기화하면 된다.

### 준영속 상태
- 영속 -> 준영속
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용하지 못한다.

### 준영속 상태로 만드는 방법
| 방법                | 설명                 |
|-------------------|--------------------|
| em.detach(entity) | 특정 엔티티만 중영속 상태로 변환 |
| em.clear()        | 영속성 컨텍스트를 완전히 초기화  |
| em.close()        | 영속성 컨텍스트 종료        |
