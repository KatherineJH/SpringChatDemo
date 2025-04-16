// import React, { useEffect, useState } from "react";
// import { Client } from "@stomp/stompjs";
// import axios from "axios";

// const BACKEND_URL = "http://localhost:8282";

// const ChatRoom = () => {
//   const [client, setClient] = useState(null);
//   const [message, setMessage] = useState("");
//   const [chatLog, setChatLog] = useState([]);
//   const [isLoggedIn, setIsLoggedIn] = useState(false);
//   const [loginForm, setLoginForm] = useState({ username: "", password: "" });

//   const chatRoomId = 1;

//   const getJwtFromCookie = () => {
//     const match = document.cookie.match(/Authorization=([^;]+)/);
//     return match ? match[1] : null;
//   };

//   const checkLoginStatus = async () => {
//     try {
//       const response = await axios.get(`${BACKEND_URL}/api/users/me`, {
//         withCredentials: true,
//         validateStatus: (status) => status < 500,
//       });

//       if (
//         typeof response.data === "string" &&
//         response.data.includes("<html")
//       ) {
//         console.log("❌ 인증 실패: HTML 응답");
//         setIsLoggedIn(false);
//         return;
//       }

//       console.log("✅ 로그인 사용자:", response.data);
//       setIsLoggedIn(true);
//     } catch (err) {
//       console.log("❌ 로그인 확인 실패", err);
//       setIsLoggedIn(false);
//     }
//   };

//   const handleLogin = async () => {
//     try {
//       await axios.post(`${BACKEND_URL}/api/auth/login`, loginForm, {
//         withCredentials: true,
//       });
//       checkLoginStatus();
//     } catch (err) {
//       alert("로그인 실패");
//     }
//   };

//   useEffect(() => {
//     checkLoginStatus();
//   }, []);

//   useEffect(() => {
//     if (!isLoggedIn) return;

//     const jwt = getJwtFromCookie();
//     if (!jwt) return;

//     const stompClient = new Client({
//       //   brokerURL: `${BACKEND_URL.replace("http", "ws")}/ws?token=${jwt}`,
//       webSocketFactory: () =>
//         new SockJS("http://localhost:8282/ws", undefined, {
//           withCredentials: true, // 이거 추가
//         }),
//       reconnectDelay: 5000,
//       onConnect: () => {
//         console.log("✅ WebSocket 연결됨");

//         stompClient.subscribe(`/topic/room/${chatRoomId}`, (message) => {
//           const payload = JSON.parse(message.body);
//           console.log("📥 수신:", payload);
//           setChatLog((prev) => [...prev, payload]);
//         });

//         stompClient.publish({
//           destination: "/app/chat.addUser",
//           body: JSON.stringify({
//             chatRoomId,
//             type: "JOIN",
//             content: "사용자 입장",
//           }),
//         });
//       },
//     });

//     stompClient.activate();
//     setClient(stompClient);

//     return () => {
//       stompClient.deactivate();
//     };
//   }, [isLoggedIn]);

//   const sendMessage = () => {
//     if (client && client.connected) {
//       client.publish({
//         destination: "/app/chat.sendMessage",
//         body: JSON.stringify({
//           chatRoomId,
//           content: message,
//           type: "CHAT",
//         }),
//       });
//       setMessage("");
//     }
//   };

//   if (!isLoggedIn) {
//     return (
//       <div style={{ padding: 20 }}>
//         <h2>🔐 로그인</h2>
//         <input
//           placeholder="이메일"
//           value={loginForm.username}
//           onChange={(e) =>
//             setLoginForm({ ...loginForm, username: e.target.value })
//           }
//         />
//         <br />
//         <input
//           type="password"
//           placeholder="비밀번호"
//           value={loginForm.password}
//           onChange={(e) =>
//             setLoginForm({ ...loginForm, password: e.target.value })
//           }
//         />
//         <br />
//         <button onClick={handleLogin}>로그인</button>
//       </div>
//     );
//   }

//   return (
//     <div style={{ padding: 20 }}>
//       <h2>💬 채팅방 #{chatRoomId}</h2>
//       <div
//         style={{
//           border: "1px solid gray",
//           height: 300,
//           overflowY: "scroll",
//           marginBottom: 10,
//         }}
//       >
//         {chatLog.map((msg, index) => (
//           <div key={index}>
//             <strong>{msg.senderUsername || "?"}:</strong> {msg.content}
//           </div>
//         ))}
//       </div>
//       <input
//         value={message}
//         onChange={(e) => setMessage(e.target.value)}
//         onKeyDown={(e) => e.key === "Enter" && sendMessage()}
//         placeholder="메시지를 입력하세요"
//       />
//       <button onClick={sendMessage}>전송</button>
//     </div>
//   );
// };

// export default ChatRoom;
