import React, { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";

const ChatRoom = () => {
  const [client, setClient] = useState(null);
  const [message, setMessage] = useState("");
  const [chatLog, setChatLog] = useState([]);
  const isConnected = useRef(false); // ✅ 중복 방지용 ref
  const chatRoomId = 1;

  useEffect(() => {
    if (isConnected.current) return; // ✅ 중복 방지용 ref
    isConnected.current = true;

    const fetchUserAndConnect = async () => {
      try {
        // 1. 사용자 인증 확인
        const userRes = await fetch("http://localhost:8282/api/users/me", {
          credentials: "include",
        });

        if (!userRes.ok) throw new Error("인증 실패");

        const user = await userRes.json();
        console.log("🟢 인증된 사용자:", user.username);

        // 2. 서버에서 토큰 꺼내오기
        const tokenRes = await fetch("http://localhost:8282/api/auth/token", {
          credentials: "include",
        });

        const { token } = await tokenRes.json();
        console.log("🪪 토큰:", token);

        // 3. WebSocket 연결
        const stomp = new Client({
          // SockJS 안쓰는 방식
          brokerURL: `ws://localhost:8282/ws?token=${token}`, // ✅ ws:// 사용
          debug: (str) => console.log(str),
          onConnect: () => {
            console.log("✅ STOMP 연결됨");

            // 4. 채팅방 구독
            stomp.subscribe(`/topic/room/${chatRoomId}`, (msg) => {
              const body = JSON.parse(msg.body);
              if (body.type === "JOIN") {
                setChatLog((prev) => [...prev, `🟢 ${body.content}`]);
              } else if (body.type === "LEAVE") {
                setChatLog((prev) => [...prev, `🔴 ${body.content}`]);
              } else if (body.type === "CHAT") {
                setChatLog((prev) => [
                  ...prev,
                  `${body.senderUsername}: ${body.content}`,
                ]);
              }
            });

            // ✅ 퇴장 메시지 구독
            stomp.subscribe(`/topic/public`, (msg) => {
              const body = JSON.parse(msg.body);
              if (body.messageType === "LEAVE") {
                setChatLog((prev) => [...prev, `👋 ${body.content}`]);
              }
            });

            // 5. 입장 메시지 전송
            stomp.publish({
              destination: "/app/chat.addUser",
              body: JSON.stringify({ chatRoomId }),
            });
          },
          onStompError: (frame) => {
            console.error("❌ STOMP 에러:", frame);
          },
        });

        stomp.activate();
        setClient(stomp);
      } catch (err) {
        console.error("❌ 인증 실패:", err);
      }
    };

    fetchUserAndConnect();
  }, []);

  const handleSend = () => {
    if (client && client.connected) {
      client.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify({
          chatRoomId,
          content: message,
          type: "CHAT",
        }),
      });
      setMessage("");
    } else {
      alert("❌ 연결되지 않았습니다.");
    }
  };

  return (
    <div style={{ padding: "2rem", maxWidth: "600px", margin: "auto" }}>
      <h2>채팅방</h2>
      <div
        style={{
          border: "1px solid #ccc",
          padding: "1rem",
          height: "300px",
          overflowY: "auto",
          marginBottom: "1rem",
        }}
      >
        {chatLog.map((msg, idx) => (
          <div key={idx}>{msg}</div>
        ))}
      </div>
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        style={{ width: "80%", marginRight: "1rem", padding: "0.5rem" }}
      />
      <button onClick={handleSend} style={{ padding: "0.5rem 1rem" }}>
        보내기
      </button>
    </div>
  );
};

export default ChatRoom;
