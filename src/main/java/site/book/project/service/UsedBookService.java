package site.book.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookPost;
import site.book.project.dto.MarketCreateDto;
import site.book.project.repository.UsedBookImageRepository;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.repository.UsedBookRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsedBookService {
	// 부끄마켓 글 하나는 세개의 테이블로 되어 있음.
	// service를 하나로 뭉쳐서 만들거임
	
	private final UsedBookRepository usedBookRepository;
	private final UsedBookPostRepository postRepository;
	private final UsedBookImageRepository imgRepository;
	
	
	/**(은정)
	 * 책 검색 후 바로 UsedBook테이블에 저장하여 UsedBookPost와 UsedBookImage에 연결할 수 있는
	 * FK를 리턴해줌. 잘하면 임시저장할 수 있지 않을까?
	 * @param bookId 판매할 책의 PK 
	 * @param userId 판매하는 사용자의 PK
	 * @return 생성된 UsedBook의 PK
	 */
	public Integer create(Integer bookId, Integer userId) {
		
		UsedBook usedBook = usedBookRepository.save(UsedBook.builder().userId(userId).bookId(bookId).build());
		
		return usedBook.getId();
	}
	
	/**(은정)
	 * UsedBook는 Transactional를 통한 update
	 * UsedBookContent는 객체 생성 후 save
	 * @param usedBookId UsedBook의 PK
	 * @param dto market/create에서 받은 데이터(사진 DB제외)
	 */
	@Transactional
	public void create(Integer usedBookId, MarketCreateDto dto ) {
		
		// UsedBook 디비에 저장
		UsedBook entity = usedBookRepository.findById(usedBookId).get();
		entity.update(dto);
		
		// UsedBookPost에 저장
		postRepository.save(UsedBookPost.builder().usedBookId(usedBookId).content(dto.getContents()).build());
		
	}





	
    
    
}