# DB 테스트
- DB name은 db_chat, 바꾸셔도 괜찮습니다.
- POST localhost:8282/api/auth/register 으로 유저 두명 이상 만들어주세요.
  - 요청 Body  {
    "username": "user1@gmail.com",
    "password": "user1"
    }
- POST http://localhost:8282/api/chat/initiate/1?requesterId=2
  - 앞의 숫자는 post id, 뒤의 숫자는 글을 게시하는 유저 id로 채팅룸을 생성해주세요

# Server(demo 폴더입니다)
- OAuth로 가입하는 유저는 아직 테스트 하지 않았습니다.
- Entity 구성도 테스트이며, 데모 프로젝트이므로 감안하여주세요.

# FE(chat-client 폴더입니다)
### 메인 페이지
- http://localhost:5173/
- 로그인 페이지 입니다. 현재 로그인 된 상태여도 항상 로그인 화면이 확인되므로 로그인 된 상태라면 바로 http://localhost:5173/chat으로 이동하면 됩니다.

### 채팅 페이지
- http://localhost:5173/chat
- 크롬 incognito(시크릿)모드로 윈도우 창을 두개 띄우고 각자 다른 아이디로 로그인을 해주세요.
- Authorization 네임을 가진 쿠키에서 토큰 확인 가능합니다. 로깅 확인도 가능합니다.
- 각 유저가 입장하고, 나갈 때 각각 다른 메시지 확인 가능합니다.
