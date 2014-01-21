package net.juniper.jmp.monitor.mo.info;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author juntaod
 *
 */
public class MemSummary implements Serializable{
	private static final long serialVersionUID = 6116829712069628467L;
	private String series;
	private List<Integer> values = new ArrayList<Integer>();
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public List<Integer> getValues() {
		return values;
	}
	public void setValues(List<Integer> values) {
		this.values = values;
	}
	
	public void queue(Integer value){
		this.values.remove(0);
		this.values.add(value);
	}
}
