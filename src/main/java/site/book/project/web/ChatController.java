package site.book.project.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chat;
import site.book.project.domain.UsedBook;
import site.book.project.domain.User;
import site.book.project.dto.ChatListDto;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.ChatRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UserRepository;
import site.book.project.service.ChatService;

@Slf4j
@Controller
public class ChatController {
    
    // 채팅 view 접속하기
//    @GetMapping("/chat")
//    public String onChatting(String withChatUsername, Model model) {
//        log.info("withChatUsername={}", withChatUsername);
//        model.addAttribute("firstConnectUser", withChatUsername);
//        return "chat";
//    }
    
    // 
//    @GetMapping("message")
//    @SendTo("/chat/message")
//    public String getMessage(String message) {
//        return message;
//    }
    
    // (지혜)
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsedBookRepository usedBookRepository;
 
    // 중고판매글에서 '채팅하기' 버튼 클릭시
    @PostMapping("/chat")
    @ResponseBody
    public String getWebSocketWithSockJs(@AuthenticationPrincipal UserSecurityDto userDto, Integer usedBookId, Integer sellerId) throws IOException {
        
        Integer buyerId = userDto.getId();
        
        log.info("채팅 존재여부 확인: usedBookId={}, sellerId={}", usedBookId, sellerId);
        
        //이미 chat이 만들어져있는지 확인
        Chat chatExistsOrNot = chatRepository.findByUsedBookIdAndBuyerId(usedBookId, buyerId);
        
        if (chatExistsOrNot != null) {
            // 이미 채팅을 하고 있다면
            log.info("이미 채팅 중입니다!");
            return "/chat?chatRoomId="+chatExistsOrNot.getChatRoomId();
            
        } else {
            // 새로운 채팅 시작이라면
            log.info("새 채팅을 시작합니다!");
            // chat 생성 (+ txt 파일 생성)       
            Integer newChatRoomId = chatService.createChat(usedBookId, sellerId, buyerId);       
            return "/chat?chatRoomId="+newChatRoomId;
        }
    }
    
    
    @GetMapping("/chat")
    public void showChatWindow(@AuthenticationPrincipal UserSecurityDto userDto, Integer chatRoomId, Model model) throws IOException {
        log.info("챗창 오픈 Get Mapping");
        
        Integer loginUserId = userDto.getId();
        User loginUser = userRepository.findById(loginUserId).get();
        
        // 뷰에 보여 줄 내 정보
        model.addAttribute("loginUser", loginUser);
        
        List<ChatListDto> list = chatService.loadChatList(loginUserId);
        
        // 뷰에 보여 줄 채팅방 정보들(리스트)
        model.addAttribute("data", list);
        
        List<Chat> myChats = chatRepository.findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(loginUserId, loginUserId);
        
            // chatHistory 불러 오기
            List<ChatReadDto> chatHistory = chatService.readChatHistory(myChats.get(0));
            //chatHistory Model에 저장해 View로 전달
            model.addAttribute("chatHistory", chatHistory);    // (주의) 지금은 가장 최신 채팅방 히스토리만 보이는 상태! JS 작업 해야 함
    }
    
    // (홍찬) 내 대화 목록 불러오기
    @GetMapping("/chat/list")
    public String openMyChatList(@AuthenticationPrincipal UserSecurityDto userDto, Model model) throws IOException {
        Integer loginUserId = userDto.getId();

        log.info("잘 도착햇나{}",loginUserId);
        // 최근에 업데이트된 날짜 순으로 받아온 내가 대화중인 대화들
        List<Chat> chat = chatRepository.findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(loginUserId, loginUserId);
        List<String> cl = new ArrayList<>();
        for (Chat c : chat) {
            log.info("방번호{}",c.getChatRoomId());
            cl.add(chatService.readLastThreeLines(c));
        }
        
        model.addAttribute("myChatList" ,cl);
        return "";
    }
}