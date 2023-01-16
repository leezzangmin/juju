<h1 align="middle"> G사 주주총회 전자 투표 시스템 </h1>
### Summer(이창민)  

## 요구사항 분석:
프로덕트의 목적과 필요를 파악하고 어떤 방식으로 풀어나갈지 결정

- 회원 기능(인증/인가)
    - 비회원은 사용 불가
    - normal, admin, 휴면 3가지로 분류

- 안건 생성, 삭제
    - admin 유저만 가능

- 안건 조회
    - 목록 조회 기능

- 투표 (표를 행사하는 기능)
    - 의결권 개수만큼 투표 가능
    - 무제한투표 / 선착순투표 구분된 API로 설계
    - 동시에 요청이 몰려도 순서대로 데이터 정합성이 어긋나지 않고 수행되어야 함
    - 상태 3가지 - ing, before, done
-
- 투표 게시
    - 안건에 대해 투표를 생성 - admin 유저만 가능
    - 게시하는 시점에 직접 종료 시간을 통보하여야 함 (모호)
    - 투표 게시(생성) 시점에 선착순/무제한 투표 구분하여 생성

- 투표 종료
    - 수동 종료의 경우 admin 유저만 가능
    - 수동으로 종료되지 않은 투표라면 투표 종료 시간 ‘이후에’ 시스템이 자동으로 종료 (이후에 라는 제약 ?)

- 투표 조회
    - 투표가 완료된 안건에 대해 조회하는 API 필요 - 일반 유저에게도 오픈됨
    - 해당 API는 투표목록, 숫자(찬성, 반대, 기권)를 제공해야 함
    - admin 유저는 동일한 API를 통해 어떤 사용자가 해당 안건에 어떤 의사를 표했는지 + 의결권 몇개를 사용했는지 제공받음

- 투표 로깅
    - 투표가 조작되지 않고 투명성과 정합성을 증명하기 위해 내역을 실시간으로 기록해야함
    - 로깅한 내용을 조회하는 API는 요구사항에 존재하지 않으나 추후 요구사항이 발생할 수 있으므로 private하게 개발 + 명세 작성하는 것이 필요해보임

- API 명세서
    - 제공하는 API에 대해서 명세서를 제작해 제공

# 제한사항

1. 요구사항 8번에 해당하는 의결권 선착순 제한 경쟁 방식은 여러 주주가 동시에 제한 경쟁에 참여하더라도 정상 동작함을 보장해야 한다. 이를 테스트 코드를 통해 검증이 가능해야 한다.
2. 요구사항 9번에 해당하는 투표 시스템은 테스트 코드를 통해 검증할 수 있어야 한다.
3. 버전 관리는 GIt을 사용하며, 데일리 커밋 정책이 적용되므로 구현한 기능에 대해 최소 하루에 한 번 이상 커밋을 수행 해야 한다.

중점으로 두고 개발할 것 :

1. 정상 작동
2. 성능

## ERD:

![image](/uploads/07553da07b8890a7497a6288baf1f0a1/image.png)
## 로그인 플로우:

### 1차 스택 및 플로우 구현방향:
- GABIA hiworks Oauth
- JWT
- Spring Security (는 배우면서 적용하기에 시간이 부족할 듯)  

![Oauth_로그인_플로우](/uploads/d1f35fa3ba74b6b84366dd40ad277a1a/Oauth_로그인_플로우.png)  
1. 사용자가 스프링부트 프로젝트의 [로그인url api]로 요청하면 gabia의 로그인 링크로 redirect 혹은 url  + client_id, scope,redirect_url 리턴
2. 리턴된 링크에서 로그인을 수행하면 미리 설정된 redirect_url로 authorization code와 함께 새로고침됨
3. 사용자 모르게 스프링부트의 redirect_url에 authorization_code를 담아 요청
4. 스프링부트는 gabia oauth 서버의 access_token 발급 api에 authorization_code, client_id, client_secrets 를 담아 요청
5. gabia oauth 서버는 응답으로 access_token, scope, token_type을 json형식으로 리턴
6. 리턴받은 정보(access_token)로 이제 실제 사용자의 정보를 gabia oauth(resource) 서버에게 요청
7. gabia oauth(resource) 서버는 토큰 유효성 확인 후 user의 정보를 리턴
8. 스프링부트 서버는 리턴받은 user의 정보를 upsert
9. JWT 생성 후 사용자에게 발급

### 2차 구현방향:
- ID/PW 평문 전송
- 어플리케이션 쿠키/세션

1. 회원가입은 이미 되어있다고 가정함
2. 사용자가`/login` url로 ID와 PASSWORD를 BODY에 JSON으로 담아 POST 요청
3. 서버는 ID가 존재하는지, ID와 패스워드가 일치하는지를 검증
4. 검증되면 회원 구분(일반,관리자,휴면) 에 맞는 쿠키 생성 후 발급
5. 사용자는 발급받은 쿠키로 API 이용
6. 일단 이렇게 구현하고 점진적으로 발전시키기

## API Specification
[Postman API Link](https://documenter.getpostman.com/view/19902575/2s8Z76v8wZ)


## 프로젝트를 진행하며 고민했던 내용  

### 1. 트랜잭션의 범위와 개수  
OSIV를 off하고, Service 레이어에서만 트랜잭션이 작동하도록 신경쓰며 개발.  
컨트롤러 레이어에서 트랜잭션 서비스를 두번 이상 호출하거나 트랜잭션에 포함되지 않아도 되는 검증로직과 DTO 생성로직 등은 모두 제외시켜서 커넥션 갯수와 커넥션 시간을 가능한 작게 유지시킴.  
ex)  
쿠키 세션 특성상, 세션에 최소한의 정보만 담겨있기 때문에 추가적인 DB 조회가 필요할 수 있음.  
```java
public class SessionMemberAuthDTO {
    private Long memberId;
    private MemberGubun memberGubun;
}

@GetMapping("/vote/{voteId}")
public ResponseEntity<VoteResponseDTO> findVote(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, @PathVariable Long voteId) {
    // memberService.findByMemberId(memberId); ?
    if (sessionMemberAuthDTO.getMemberGubun().equals(MemberGubun.ADMIN)) {
    VoteResponseDTO voteResponseDTO = voteService.adminFindOne(voteId);
    return ResponseEntity.ok(voteResponseDTO);
  }
  VoteResponseDTO voteResponseDTO = voteService.findOne(voteId);
  return ResponseEntity.ok(voteResponseDTO);
}
```
세션에 memberId 뿐 아니라 DB에 저장된 정보인 회원역할 정보까지 함께 담겨있음.  
초기에 인증을 수행할 때 세션에 회원역할 정보를 함께 담아서 추후 인가가 필요할 때 DB를 거치지 않고도 회원의 자격을 검증할 수 있음.  

### 2. API의 성능  

캐싱이 유리한 데이터(연산이 오래걸리고 자주 쓰이며 데이터가 쉽게 변하지 않는)의 종료투표조회 기능의 캐싱을 수행했음.
<br><br>
검증 로직이 혼재해서 가독성이 좋지 않은 기존 코드:
```java
@Transactional(readOnly = true)
public VoteResponseDTO findOne(Long memberId, Long voteId) {
    MemberAuth memberAuth = memberAuthRepository.findByMemberId(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 회원이 존재하지 않습니다."));

    Vote vote = voteRepository.findById(voteId)
            .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 투표가 존재하지 않습니다."));
    validateVoteStatusIsDone(vote);

    List<VoteHistory> voteHistories = voteHistoryRepository.findAllByReferenceVoteId(voteId);
    Map<VoteHistoryActionGubun, Long> statistics = calculateCommonVoteHistoryStatistics(voteHistories);

    if (memberAuth.getMemberGubun().equals(MemberGubun.NORMAL)) {
        return VoteNormalResponseDTO.of(voteId, statistics);
    }
    return VoteAdminResponseDTO.of(voteId, statistics, voteHistories);
}
```
캐싱과 리팩토링을 진행한 코드:
```java
@Transactional
public VoteResponseDTO findOne(Long voteId) {
    Vote vote = voteRepository.findById(voteId)
            .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 투표가 존재하지 않습니다."));
    validateVoteStatusIsDone(vote);

    if (voteResultRepository.existsByReferenceVoteId(voteId)) {
        return voteResultRepository.findByReferenceVoteIdSimpleNormalDTO(voteId);
    }

    VoteResult voteResult = calculateVote(voteId);
    voteResultRepository.save(voteResult);

    return VoteNormalResponseDTO.of(voteResult);
}
```
<br>
위 코드의 문제점 ?
<br>
<br>
<br>
<br>
스레드 동시 요청시 초기화 여러번 수행될 가능성 존재  

개선 코드: 
```java
public interface NamedLockRepository extends JpaRepository<VoteResult, Long> {
  @Query(value = "select get_lock(:key, 10000)", nativeQuery = true)
  void getNamedLock(@Param("key") String key);
  @Query(value = "select release_lock(:key)", nativeQuery = true)
  void releaseNamedLock(@Param("key") String key);
}

@Transactional
public VoteResponseDTO findOne(Long voteId) {
  Vote vote = voteRepository.findById(voteId)
  .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 투표가 존재하지 않습니다."));
  validateVoteStatusIsDone(vote);
  
  namedLockRepository.getNamedLock(voteId.toString());
  
  if (voteResultRepository.existsByReferenceVoteId(voteId)) {
  namedLockRepository.releaseNamedLock(voteId.toString());
  return voteResultRepository.findByReferenceVoteIdSimpleNormalDTO(voteId);
  }
  
  VoteResult voteResult = calculateVote(voteId);
  voteResultRepository.save(voteResult);
  
  namedLockRepository.releaseNamedLock(voteId.toString());
  return VoteNormalResponseDTO.of(voteResult);
}
```


<br><br><br>
추가적으로, 안건 목록 조회 기능의 경우 커버링 인덱스로 페이징 쿼리를 최적화.
```java
@Transactional(readOnly = true)
public AgendaPageDTO agendaPagination(Pageable pageable) {
    List<Long> agendaIds = agendaRepository.findIdsByPage(pageable);
    List<Vote> agendasAndVotes = voteRepository.findVoteWithAgendaByIds(agendaIds);
    return AgendaPageDTO.of(agendasAndVotes);
}
```
JPA의 pageable 객체를 통해 생성되는 SQL은 `limit {offset},{size}` 방식을 사용.  
mysql 디폴트 스토리지 엔진 innodb의 경우 `limit {offset},{size}` 방식의 쿼리는 병목이 존재.  
from 절 서브쿼리로 풀 수 있지만 jpa에서 지원하지 않는 문법. 따라서 쿼리를 2 개로 분리.
1. 커버링 인덱스 `limit {offset},{size}` 쿼리로 빠르게 PK만 먼저 조회
2. 조회된 PK를 `where in` 절에 넣어 `select ... from ... where in (...)` 문법으로 빠르게 엔티티 조회
<br><br>


### 3. Custom ArgumentResolver + 쿠키/세션 을 사용한 인증/인가  
로그인 API를 제외한 모든 API에서 권한이 필요함
컨트롤러 레이어에서 회원의 인증과 권한을 하나하나 검증하기에는 중복코드가 너무 많아짐  
```java
@PostMapping("/login")
public ResponseEntity<String> requestLogin(@Valid @RequestBody IdPwDTO idPwDTO, HttpServletResponse response) {
    ...
    response.addCookie(newCookie);
    ...
}

// 기존 코드
@PatchMapping("/vote/{voteId}")
public ResponseEntity<VoteCloseResponseDTO> closeVote(HttpServletRequest request, @PathVariable Long voteId) {
  request.getCookies()
  cookie 검증,
  세션 정보 추출
  중복로직
  ...
}

// 개선 코드

public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

  private final LoginService loginService;
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean hasAuthAnnotation = parameter.hasParameterAnnotation(Auth.class);
    boolean hasSessionMemberAuthDTO = SessionMemberAuthDTO.class.isAssignableFrom(parameter.getParameterType());

    return hasAuthAnnotation && hasSessionMemberAuthDTO;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    SessionMemberAuthDTO sessionMemberAuthDTO = loginService.extractMemberAuthResourceFromCookie(request);
    return sessionMemberAuthDTO;
  }
}

@PatchMapping("/vote/{voteId}")
public ResponseEntity<VoteCloseResponseDTO> closeVote(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, @PathVariable Long voteId) {
  ...
}
```

## 아쉬운 점
인증/인가를 열심히 못했음  
예외처리  
새로운 것을 배우고 적용하기엔 시간이 짧아서 도전을 많이 하지 못한 것. ex) 레디스 세션 및 분산락  
TODO: 모든 api에서 발생하는 쿼리 실행계획 문서화 + 튜닝
