package com.borjabares.pan_ssh.model.user;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.borjabares.pan_ssh.util.GlobalNames.Level;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Entity
@Validations(requiredFields = { @RequiredFieldValidator(fieldName = "age", message = "La edad es un campo obligatorio.", key = "error.age.required", shortCircuit = true) }, requiredStrings = {
		@RequiredStringValidator(fieldName = "username", message = "Debe proporcionar un nombre de usuario", key = "error.username.required", shortCircuit = true),
		@RequiredStringValidator(fieldName = "login", message = "Debe proporcionar un login", key = "error.login.required", trim = true, shortCircuit = true),
		@RequiredStringValidator(fieldName = "password", message = "Debe proporcionar una contraseña", key = "error.password.required", trim = true, shortCircuit = true) }, stringLengthFields = {
		@StringLengthFieldValidator(fieldName = "username", minLength = "3", maxLength = "15", message = "El nombre del usuario debe tener entre ${minLength} y ${maxLength} caracteres.", key = "error.username.length"),
		@StringLengthFieldValidator(fieldName = "login", minLength = "4", maxLength = "15", message = "El login debe tener entre ${minLength} y ${maxLength} caracteres.", key = "error.login.length"),
		@StringLengthFieldValidator(fieldName = "password", minLength = "6", maxLength = "15", message = "La contraseña debe tener entre ${minLength} y ${maxLength} caracteres.", key = "error.password.length") }, intRangeFields = { @IntRangeFieldValidator(fieldName = "age", min = "12", max = "110", message = "Debe tener entre ${min} y ${max} años.", key = "error.age.range") }, expressions = { @ExpressionValidator(expression = "!login.equals(password)", message = "El login no puede ser igual que la contraseña", key = "error.passlog.same") })
public class User {

	private long userId;
	private String username;
	private String login;
	private String password;
	private Calendar created;
	private Calendar lastlogin;
	private float karma;
	private Level level;
	private String lang;
	private String url;
	private String email;
	private String ip;
	private long version;

	public User() {
	}

	public User(String login, String password, String email, String ip) {
		this.login = login;
		this.password = password;
		this.email = email;
		this.ip = ip;
		this.created = Calendar.getInstance();
		this.lastlogin = Calendar.getInstance();
		this.karma = 10.0F;
		this.level = Level.NORMAL;
		this.lang = "es";
	}

	@SequenceGenerator(name = "UserIdGenerator", sequenceName = "UserSeq")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "UserIdGenerator")
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(Calendar lastlogin) {
		this.lastlogin = lastlogin;
	}

	public float getKarma() {
		return karma;
	}

	public void setKarma(float karma) {
		this.karma = karma;
	}

	@Enumerated(EnumType.STRING)
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Version
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "User [ \nuserId=" + userId + ", \nusername=" + username
				+ ", \nlogin=" + login + ", \npassword=" + password
				+ ", \ncreated=" + created.get(Calendar.DAY_OF_MONTH) + "/" + (created.get(Calendar.MONTH)
				+ 1 )+ "/" + created.get(Calendar.YEAR) + ", \nlastlogin="
				+ lastlogin.get(Calendar.DAY_OF_MONTH) + "/" + (lastlogin.get(Calendar.MONTH) + 1) + "/"
				+ lastlogin.get(Calendar.YEAR) + ", \nkarma=" + karma + ", \nlevel=" + level
				+ ", \nlang=" + lang + ", \nurl=" + url + ", \nemail=" + email
				+ ", \nip=" + ip + ", \nversion=" + version + "]";
	}

}
