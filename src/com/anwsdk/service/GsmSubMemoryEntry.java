package com.anwsdk.service;

public class GsmSubMemoryEntry {
//	private static final int GSM_PHONEBOOK_TEXT_LENGTH = 200;

	public enum GsmEntryType {
		// General number. (Text)
		PBK_Number_General(1),

		// Mobile number. (Text)
		PBK_Number_Mobile(2),

		// Work number. (Text)
		PBK_Number_Work(3),

		// Fax number. (Text)
		PBK_Number_Fax(4),

		// Home number. (Text)
		PBK_Number_Home(5),

		// Pager number. (Text)
		PBK_Number_Pager(6),

		// Other number. (Text)
		PBK_Number_Other(7),

		// Note. (Text)
		PBK_Text_Note(8),

		// Complete postal address. (Text)
		PBK_Text_Postal(9),

		// Email. (Text)
		PBK_Text_Email(10),

		// Second email. (Text)
		PBK_Text_Email2(11),

		// URL (Text)
		PBK_Text_URL(12),

		// Date and time. FIXME: describe better (Date)
		PBK_Date(13),

		// Caller group. (Text)
		PBK_Caller_Group(14),

		// Name (Text)
		PBK_Text_Name(15),

		// Last name. (Text)
		PBK_Text_LastName(16),

		// First name. (Text)
		PBK_Text_FirstName(17),

		// Company. (Text)
		PBK_Text_Company(18),

		// Job title. (Text)
		PBK_Text_JobTitle(19),

		// Category. (Number)
		PBK_Category(20),

		// Whether entry is private. (Number)
		PBK_Private(21),

		// Street address. (Text)
		PBK_Text_StreetAddress(22),

		// City. (Text)
		PBK_Text_City(23),

		// State. (Text)
		PBK_Text_State(24),

		// Zip code. (Text)
		PBK_Text_Zip(25),

		// Country. (Text)
		PBK_Text_Country(26),

		// Custom information 1. (Text)
		PBK_Text_Custom1(27),

		// Custom information 2. (Text)
		PBK_Text_Custom2(28),

		// Custom information 3. (Text)
		PBK_Text_Custom3(29),

		// Custom information 4. (Text)
		PBK_Text_Custom4(30),

		// Ringtone ID. (Number)
		PBK_RingtoneID(31),

		// Ringtone ID in phone filesystem. (Number)
		PBK_RingtoneFileSystemID(32),

		// Picture ID. (Number)
		PBK_PictureID(33),
		PBK_SMSListID(34),

		// User ID. (Text)
		PBK_Text_UserID(35),

		// PictureName (Text)
		PBK_Text_Picture(36),

		// RingName (Text)
		PBK_Text_Ring(37),

		// Sex
		PBK_Number_Sex(38),

		// Sex
		PBK_Number_Light(39),
		// Caller group. (Text)

		//Compare : Push Talk. (TEXT) // Added by Mingfa 0127; 
	    PBK_Push_Talk(40),

		PBK_Caller_Group_Text(41),

		// Street address. (Text)
		PBK_Text_StreetAddress2(42),

		//Nickname (Text)
		PBK_Text_Nickname(43),

		PBK_Number_Mobile_Home(44),
		PBK_Number_Mobile_Work(45),
		PBK_Number_Fax_Home(46),
		PBK_Number_Fax_Work(47),
		PBK_Text_Email_Home(48),
		PBK_Text_Email_Work(49),
		PBK_Text_URL_Home(50),
		PBK_Text_URL_Work(51),
		PBK_Text_Postal_Home(52),
		PBK_Text_Postal_Work(53),
		PBK_Number_Pager_Home(54),
		PBK_Number_Pager_Work(55),
		PBK_Number_VideoCall(56),
		PBK_Number_VideoCall_Home(57),
		PBK_Number_VideoCall_Work(58),
		PBK_Text_MiddleName(59),
		PBK_Text_Suffix(60),
		PBK_Text_Title(61),
		PBK_Text_Email_Mobile(62),
		PBK_Text_Email_Unknown(63),

		//new add
		PBK_Number_Assistant(64),
		PBK_Number_Business(65),
		PBK_Number_Callback(66),
		PBK_Number_Car(67),
		PBK_Number_ISDN(68),
		PBK_Number_Primary(69),
		PBK_Number_Radio(70),
		PBK_Number_Telix(71),
		PBK_Number_TTYTDD(72),

		PBK_Text_Department(73),
		PBK_Text_Office(74),
		PBK_Text_Profession(75),
		PBK_Text_Manager_Name(76),
		PBK_Text_Assistant_Name(77),
		PBK_Text_Spouse_Name(78),
		PBK_Date_Anniversary(79),
		PBK_Text_Directory_Server(80),
		PBK_Text_Email_alias(81),
		PBK_Text_Internet_Address(82),
		PBK_Text_Children(83),

		PBK_Text_StreetAddress_Work(84),
		PBK_Text_City_Work(85),
		PBK_Text_State_Work(86),
		PBK_Text_Zip_Work(87),
		PBK_Text_Country_Work(88),

		PBK_Text_StreetAddress_Home(89),
		PBK_Text_City_Home(90),
		PBK_Text_State_Home(91),
		PBK_Text_Zip_Home(92),
		PBK_Text_Country_Home(93),
		PBK_Text_IMID(94);
		
		private final int id;
		
		GsmEntryType(int id) { this.id = id;}
		public int getValue() { return id; }
	}
	
	/*
	 * Type of entry
	 * C type : GSM_EntryType
	 */
	public GsmEntryType EntryType;
	/*
	 * Text of entry (if applicable, see @ref GSM_EntryType).<br>
	 * C type : unsigned char[(GSM_PHONEBOOK_TEXT_LENGTH + 1) * 2]
	 * GSM_PHONEBOOK_TEXT_LENGTH = 200
	 */
	//public byte[] Text = new byte [(GSM_PHONEBOOK_TEXT_LENGTH+1)*2];
	public String Text;
	/*
	 * Text of entry (if applicable, see @ref GSM_EntryType).<br>
	 * C type : GSM_DateTime
	 */
	public GsmDateTime Date;
	// Number of entry (if applicable, see @ref GSM_EntryType).
	public int Number;
	// Voice dialling tag.
	public int VoiceTag;
	/*
	 *  C type : int[20]
	 */
	public int[] SMSList;/* = new int[20];*/

	public void SetEntryType(int nType) {
		//EntryType.setValue(nType);
		if(GsmEntryType.PBK_Number_General.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_General;
		else if(GsmEntryType.PBK_Number_Mobile.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Mobile;
		else if(GsmEntryType.PBK_Number_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Work;
		else if(GsmEntryType.PBK_Number_Fax.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Fax;
		else if(GsmEntryType.PBK_Number_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Home;
		else if(GsmEntryType.PBK_Number_Pager.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Pager;
		else if(GsmEntryType.PBK_Number_Other.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Other;
		else if(GsmEntryType.PBK_Text_Note.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Note;
		else if(GsmEntryType.PBK_Text_Postal.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Postal;
		else if(GsmEntryType.PBK_Text_Email.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Email;
		else if(GsmEntryType.PBK_Text_Email2.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Email2;
		else if(GsmEntryType.PBK_Text_URL.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_URL;
		else if(GsmEntryType.PBK_Date.getValue() == nType)
			EntryType = GsmEntryType.PBK_Date;
		else if(GsmEntryType.PBK_Caller_Group.getValue() == nType)
			EntryType = GsmEntryType.PBK_Caller_Group;
		else if(GsmEntryType.PBK_Text_Name.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Name;
		else if(GsmEntryType.PBK_Text_LastName.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_LastName;
		else if(GsmEntryType.PBK_Text_FirstName.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_FirstName;
		else if(GsmEntryType.PBK_Text_Company.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Company;
		else if(GsmEntryType.PBK_Text_JobTitle.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_JobTitle;
		else if(GsmEntryType.PBK_Category.getValue() == nType)
			EntryType = GsmEntryType.PBK_Category;
		else if(GsmEntryType.PBK_Private.getValue() == nType)
			EntryType = GsmEntryType.PBK_Private;
		else if(GsmEntryType.PBK_Text_StreetAddress.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_StreetAddress;
		else if(GsmEntryType.PBK_Text_City.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_City;
		else if(GsmEntryType.PBK_Text_State.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_State;
		else if(GsmEntryType.PBK_Text_Zip.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Zip;
		else if(GsmEntryType.PBK_Text_Country.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Country;
		else if(GsmEntryType.PBK_Text_Custom1.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Custom1;
		else if(GsmEntryType.PBK_Text_Custom2.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Custom2;
		else if(GsmEntryType.PBK_Text_Custom3.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Custom3;
		else if(GsmEntryType.PBK_Text_Custom4.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Custom4;
		else if(GsmEntryType.PBK_RingtoneID.getValue() == nType)
			EntryType = GsmEntryType.PBK_RingtoneID;
		else if(GsmEntryType.PBK_RingtoneFileSystemID.getValue() == nType)
			EntryType = GsmEntryType.PBK_RingtoneFileSystemID;
		else if(GsmEntryType.PBK_PictureID.getValue() == nType)
			EntryType = GsmEntryType.PBK_PictureID;
		else if(GsmEntryType.PBK_SMSListID.getValue() == nType)
			EntryType = GsmEntryType.PBK_SMSListID;
		else if(GsmEntryType.PBK_Text_UserID.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_UserID;
		else if(GsmEntryType.PBK_Text_Picture.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Picture;
		else if(GsmEntryType.PBK_Text_Ring.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Ring;
		else if(GsmEntryType.PBK_Number_Sex.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Sex;
		else if(GsmEntryType.PBK_Number_Light.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Light;
		else if(GsmEntryType.PBK_Push_Talk.getValue() == nType)
			EntryType = GsmEntryType.PBK_Push_Talk;
		else if(GsmEntryType.PBK_Caller_Group_Text.getValue() == nType)
			EntryType = GsmEntryType.PBK_Caller_Group_Text;
		else if(GsmEntryType.PBK_Text_StreetAddress2.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_StreetAddress2;
		else if(GsmEntryType.PBK_Text_Nickname.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Nickname;
		else if(GsmEntryType.PBK_Number_Mobile_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Mobile_Home;
		else if(GsmEntryType.PBK_Number_Mobile_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Mobile_Work;
		else if(GsmEntryType.PBK_Number_Fax_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Fax_Home;
		else if(GsmEntryType.PBK_Number_Fax_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Fax_Work;
		else if(GsmEntryType.PBK_Text_Email_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Email_Home;
		else if(GsmEntryType.PBK_Text_Email_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Email_Work;
		else if(GsmEntryType.PBK_Text_URL_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_URL_Home;
		else if(GsmEntryType.PBK_Text_URL_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_URL_Work;
		else if(GsmEntryType.PBK_Text_Postal_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Postal_Home;
		else if(GsmEntryType.PBK_Text_Postal_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Postal_Work;
		else if(GsmEntryType.PBK_Number_Pager_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Pager_Home;
		else if(GsmEntryType.PBK_Number_Pager_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Pager_Work;
		else if(GsmEntryType.PBK_Number_VideoCall.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_VideoCall;
		else if(GsmEntryType.PBK_Number_VideoCall_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_VideoCall_Home;
		else if(GsmEntryType.PBK_Number_VideoCall_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_VideoCall_Work;
		else if(GsmEntryType.PBK_Text_MiddleName.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_MiddleName;
		else if(GsmEntryType.PBK_Text_Suffix.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Suffix;
		else if(GsmEntryType.PBK_Text_Title.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Title;
		else if(GsmEntryType.PBK_Text_Email_Mobile.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Email_Mobile;
		else if(GsmEntryType.PBK_Text_Email_Unknown.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Email_Unknown;
		else if(GsmEntryType.PBK_Number_Assistant.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Assistant;
		else if(GsmEntryType.PBK_Number_Business.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Business;
		else if(GsmEntryType.PBK_Number_Callback.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Callback;
		else if(GsmEntryType.PBK_Number_Car.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Car;
		else if(GsmEntryType.PBK_Number_ISDN.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_ISDN;
		else if(GsmEntryType.PBK_Number_Primary.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Primary;
		else if(GsmEntryType.PBK_Number_Radio.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Radio;
		else if(GsmEntryType.PBK_Number_Telix.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_Telix;
		else if(GsmEntryType.PBK_Number_TTYTDD.getValue() == nType)
			EntryType = GsmEntryType.PBK_Number_TTYTDD;
		else if(GsmEntryType.PBK_Text_Department.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Department;
		else if(GsmEntryType.PBK_Text_Office.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Office;
		else if(GsmEntryType.PBK_Text_Profession.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Profession;
		else if(GsmEntryType.PBK_Text_Manager_Name.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Manager_Name;
		else if(GsmEntryType.PBK_Text_Assistant_Name.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Assistant_Name;
		else if(GsmEntryType.PBK_Text_Spouse_Name.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Spouse_Name;
		else if(GsmEntryType.PBK_Date_Anniversary.getValue() == nType)
			EntryType = GsmEntryType.PBK_Date_Anniversary;
		else if(GsmEntryType.PBK_Text_Directory_Server.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Directory_Server;
		else if(GsmEntryType.PBK_Text_Email_alias.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Email_alias;
		else if(GsmEntryType.PBK_Text_Internet_Address.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Internet_Address;
		else if(GsmEntryType.PBK_Text_Children.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Children;
		else if(GsmEntryType.PBK_Text_StreetAddress_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_StreetAddress_Work;
		else if(GsmEntryType.PBK_Text_City_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_City_Work;
		else if(GsmEntryType.PBK_Text_State_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_State_Work;
		else if(GsmEntryType.PBK_Text_Zip_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Zip_Work;
		else if(GsmEntryType.PBK_Text_Country_Work.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Country_Work;
		else if(GsmEntryType.PBK_Text_StreetAddress_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_StreetAddress_Home;
		else if(GsmEntryType.PBK_Text_City_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_City_Home;
		else if(GsmEntryType.PBK_Text_State_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_State_Home;
		else if(GsmEntryType.PBK_Text_Zip_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Zip_Home;
		else if(GsmEntryType.PBK_Text_Country_Home.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_Country_Home;
		else if(GsmEntryType.PBK_Text_IMID.getValue() == nType)
			EntryType = GsmEntryType.PBK_Text_IMID;
	}
	
	public void setText(String/*byte[]*/ value) {
		Text = value;
	}
	
	public String/*byte[]*/ getText() {
		return Text;
	}
	
	public void setNumber(int value) {
		Number = value;
	}

	public int getNumber() {
		return Number;
	}
	
	public void setVoiceTag(int value) {
		VoiceTag = value;
	}

	public int getVoiceTag() {
		return VoiceTag;
	}

	public void setSMSList(int[] value) {
		SMSList = value;
	}

	public int[] getSMSList() {
		return SMSList;
	}
	
	public GsmSubMemoryEntry(int/*GsmEntryType*/ entry_type, String/*byte[]*/ text, GsmDateTime date, int number, int voice_tag, int[] sms_list) {
		/*EntryType = */SetEntryType(entry_type);
		//System.arraycopy(text, 0, Text, 0, text.length);
		Text = text;
		Date = date;
		Number = number;
		VoiceTag = voice_tag;
		SMSList = new int[sms_list.length];
		System.arraycopy(sms_list, 0, SMSList, 0, sms_list.length);
	}
	
	public GsmSubMemoryEntry() {
		EntryType = GsmEntryType.PBK_Number_General;
		Text="";
		Date = new GsmDateTime();
		Number = 0;
		VoiceTag = 0;
		SMSList = new int[20];
	}
}
