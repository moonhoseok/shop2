package logic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Exchange {
	private int eno;
	private String code;
	private String name;
	private float primeamt;
	private float sellamt;
	private float buyamt;
	private String edate;
}
