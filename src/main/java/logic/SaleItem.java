package logic;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor // 매개변수가 없는 생성자 
public class SaleItem {
	private int saleid; // 주문번호
	private int seq;	// 주문상품번호
	private int itemid;	// 상품아이디
	private int quantity;// 주문상품 수량
	private Item item;	// 상품 아이디에 해당하는 상품정보
//	public SaleItem() {}
	public SaleItem(int saleid, int seq, ItemSet itemSet) {
		this.saleid = saleid;
		this.seq = seq;
		this.item = itemSet.getItem();
		this.itemid = itemSet.getItem().getId(); //상품 아이디
		this.quantity = itemSet.getQuantity(); // 주문수량
	}
	
}
