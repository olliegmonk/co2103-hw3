package edu.leicester.co2103.domain;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Session {

	@Id
	@GeneratedValue
	private long id;
	private String topic;
	private Timestamp datetime;
	private int duration;
	//TODO Check if this is required for a deleted session to be reflected in the parent module.
	/*@OneToOne(cascade = CascadeType.REFRESH)
	private Module module;*/

	public Session() {}
	
	public Session(long id, String topic, Timestamp datetime, int duration) {
		super();
		this.id = id;
		this.topic = topic;
		this.datetime = datetime;
		this.duration = duration;
	}

	public long getId() {
		return id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
