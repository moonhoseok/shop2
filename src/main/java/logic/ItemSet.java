package logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor // 멤버변수값으로 생성자 구현
public class ItemSet {
	private Item item;
	private Integer quantity;
	/*
	public ItemSet(Item item, Integer quantity) {
		super();
		this.item = item;
		this.quantity = quantity;
	}
	*/
}
