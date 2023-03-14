package site.book.project.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.BookIntro;
import site.book.project.domain.BookWish;
import site.book.project.domain.Post;
import site.book.project.domain.User;

import site.book.project.dto.UserSecurityDto;

import site.book.project.dto.BookWishDto;
import site.book.project.dto.PostListDto;
import site.book.project.dto.PostReadDto;
import site.book.project.service.BookIntroService;
import site.book.project.service.BookService;
import site.book.project.service.BookWishService;
import site.book.project.service.PostService;
import site.book.project.service.UsedBookService;
import site.book.project.service.UserService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BookDetailController {
	
	private final BookService bookService;
	private final PostService postService;
	private final UserService userService;
	
	private final BookWishService bookWishService;
	private final BookIntroService bookIntroService;
	private final UsedBookService usedBookService;
    
    @GetMapping("/detail")
    public String detail(Integer id, Model model) {
        Book book = bookService.read(id);
        BookIntro bookIntro = bookIntroService.introByIsbn(book.getIsbn());
        
        // (하은) 현제 페이지 책 제외 다른 작가 리스트
        List<Book> bookList = bookService.readAuthor(book.getAuthor());
        
        // (하은) 동일한 작가 책 정보 넘기기
        List<Book> authorOtherBook = new ArrayList<>();
        
        for (Book b : bookList) {
            if(book.getBookId() != b.getBookId()) {
                authorOtherBook.add(b);
            }
        }
        
        log.info("하은 author={}", book.getAuthor());
        model.addAttribute("authorOtherBook", authorOtherBook);
        
        // (하은) 책 디테일 창에서 부끄장터 검색창으로 연결
        Integer countUsedBook = userService.countMarket(id);
        model.addAttribute("countUsedBook", countUsedBook);
        
        // for문을 통해서 숫자를 그림으로 표현? 참고해서 고치기
        double score = book.getBookScore()/10.0;
        // 책 정보 넘기기 
        model.addAttribute("book", book);
        model.addAttribute("score", score);
        // comment 넘기기
        // post 넘기기(post글 필요)
        model.addAttribute("bookIntro", bookIntro);
     
        // choi 책 한권에 대한 post 정보 받기
        List<Post> postList = postService.findBybookId(id);
        
        model.addAttribute("postList", postList );   

	    return "book/detail";
    }
    
    // (지혜) 위시리스트 담기/취소
    // 리턴되는 문자열 -> selected: 위시리스트 테이블에 추가한 후 하트 상태를 빨강으로 변경
    // unselected: 위시리스트 테이블에서 삭제한 후 하트 상태를 빈 하트로 변경
    @ResponseBody
    @GetMapping("/book/wishList")
    public String addToWishList(@AuthenticationPrincipal UserSecurityDto userSecurityDto, Integer bookId) {
        Integer userId = userSecurityDto.getId();
        log.info("addToWishList: userId={}, bookId={}", userId, bookId);
        String wish = bookWishService.changeWishButton(userId, bookId);
        
        return wish;
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/post/create")
    public String create(@AuthenticationPrincipal UserSecurityDto userSecurityDto, Integer id, Model model) {
          log.info("책 상세(bookId={})",id);
    
          Integer userId = userSecurityDto.getId();
          log.info("userId= {}",userId);

          User user = userService.read(userId);
          model.addAttribute("user", user);
          
          Book book = bookService.read(id);
          model.addAttribute("book", book);
     
        return "post/create";
    }
	
}
