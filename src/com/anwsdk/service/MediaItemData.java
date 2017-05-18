package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaItemData implements Parcelable {
	public enum MediaItemType {
		PLAYER_ITEM_TYPE_AUDIO (0),
		PLAYER_ITEM_TYPE_VIDEO(1),
		PLAYER_ITEM_TYPE_FOLDER(2),
		PLAYER_ITEM_TYPE_INVALID(3)	;	
		private final int id;
		
		MediaItemType(int id) { this.id = id;}
		public int getValue() { return id; }
	}
	public enum MediaFolderType {
		PLAYER_FOLDER_TYPE_MIXED (0),
		PLAYER_FOLDER_TYPE_TITLES(1),
		PLAYER_FOLDER_TYPE_ALBUMS(2),
		PLAYER_FOLDER_TYPE_ARTISTS(3),
		PLAYER_FOLDER_TYPE_GENRES(4),
		PLAYER_FOLDER_TYPE_PLAYLISTS(5),
		PLAYER_FOLDER_TYPE_YEARS(6),
		PLAYER_FOLDER_TYPE_INVALID(7);	
		
		private final int id;
		
		MediaFolderType(int id) { this.id = id;}
		public int getValue() { return id; }
	}	
	
	public String	parent_uid;
	public String	uid;
//	public int	item_type; //Audio(0), Video(1), Folder(2)
	public MediaItemType item_type;
	public int	playable; // "1" -> Can Play, "0" -> Can't Play
	
	// Attributes
	public String	title;	//title
	public String	artist;	//artist
	public String	album;	//album
	public int	mediaNum;	//media number
	public int	totalMediaNum;	//total media number
	public String	genre;			//genre
	public String	playtimems;	//play time (ms)
	
//	public int	folder_type;  	
	public MediaFolderType folder_type;
	public int item_index;
	
	public void setParentID(String value) {
		parent_uid = value;
	}

	public String getParentID() {
		return parent_uid;
	}

	public void setUid(String value) {
		uid = value;
	}

	public String getUid() {
		return uid;
	}
	
	public void setItemtype(int value){
		switch(value) {
		case 0:
			item_type = MediaItemType.PLAYER_ITEM_TYPE_AUDIO;
			break;
		case 1:
			item_type = MediaItemType.PLAYER_ITEM_TYPE_VIDEO;
			break;
		case 2:
			item_type = MediaItemType.PLAYER_ITEM_TYPE_FOLDER;
			break;
		case 3:
			item_type = MediaItemType.PLAYER_ITEM_TYPE_INVALID;
			break;
		default:
			item_type = MediaItemType.PLAYER_ITEM_TYPE_INVALID;
			break;
		}
	}
	public void setItemtype(MediaItemType value) {
		item_type = value;
	}
	public MediaItemType getItemtype() {
		return item_type;
	}

	
	
	public void setPlayable(int value) {
		playable = value;
	}

	public int getPlayable() {
		return playable;
	}

	public void setTitle(String value) {
		title = value;
	}

	public String getTitle() {
		return title;
	}
	
	public void setArtist(String value) {
		artist = value;
	}

	public String getArtist() {
		return artist;
	}

	public void setAlbum(String value) {
		album = value;
	}

	public String getAlbum() {
		return album;
	}

	public void setMediaNum(int value) {
		mediaNum = value;
	}

	public int getMediaNum() {
		return mediaNum;
	}
	
	public void setTotalMediaNum(int value) {
		totalMediaNum = value;
	}

	public int getTotalMediaNum() {
		return totalMediaNum;
	}
	

	public void setGenre(String value) {
		genre = value;
	}

	public String getGenre() {
		return genre;
	}
	
	public void setPlaytimems(String value) {
		playtimems = value;
	}

	public String getPlaytimems() {
		return playtimems;
	}

	public void setFoldertype(int value){
		switch(value) {
		case 0:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_MIXED;
			break;
		case 1:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_TITLES;
			break;
		case 2:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_ALBUMS;
			break;
		case 3:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_ARTISTS;
			break;
		case 4:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_GENRES;
			break;
		case 5:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_PLAYLISTS;
			break;
		case 6:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_YEARS;
			break;
		case 7:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_INVALID;
			break;
		default:
			folder_type = MediaFolderType.PLAYER_FOLDER_TYPE_INVALID;
			break;
		}
	}
	
	public void setFoldertype(MediaFolderType value) {
		folder_type = value;
	}

	public MediaFolderType getFoldertype() {
		return folder_type;
	}
	
	public void setItemIndex(int value) {
		item_index = value;
	}

	public int getItemIndex() {
		return item_index;
	}
	
	
	public MediaItemData(String sParentUid, String sUid, int iItemtype, int iPlayable, String sTitle, String sArtist, String sAlbum,int iMedianum,int iTotalMediaNum,String sGenre,String sPlaytimems,int iFoldertype,int itemindex) {
		parent_uid = sParentUid;
		uid = sUid;
		//item_type = iItemtype;
		setItemtype(iItemtype);
		playable = iPlayable;
		// Attributes
		title = sTitle;
		artist = sArtist;		
		album = sAlbum;
		mediaNum = iMedianum;
		totalMediaNum = iTotalMediaNum;
		genre = sGenre;
		playtimems = sPlaytimems;

	//	folder_type =iFoldertype;
		setFoldertype(iFoldertype);
		item_index = itemindex;

	}
	
	public MediaItemData() {
		parent_uid = "";
		uid = "";
		item_type = MediaItemType.PLAYER_ITEM_TYPE_INVALID;
		playable = 0;
		// Attributes
		title = "";
		artist = "";		
		album = "";
		mediaNum = 0;
		totalMediaNum = 0;
		genre = "";
		playtimems = "";

		folder_type =MediaFolderType.PLAYER_FOLDER_TYPE_INVALID;
		item_index =0;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(parent_uid);
		arg0.writeString(uid);
		arg0.writeInt(item_type.id);
		arg0.writeInt(playable);		

		arg0.writeString(title);
		arg0.writeString(artist);
		arg0.writeString(album);
		arg0.writeInt(mediaNum);		
		arg0.writeInt(totalMediaNum);
		arg0.writeString(genre);	
		arg0.writeString(playtimems);
		arg0.writeInt(folder_type.id);		
		arg0.writeInt(item_index);		
	}
	public void readFromParcel(Parcel in) {
		parent_uid = in.readString();
		uid = in.readString();
		int itemtype = in.readInt();
		setItemtype(itemtype);
		playable = in.readInt();
		
		title = in.readString();
		artist = in.readString();
		album = in.readString();
		mediaNum = in.readInt();
		totalMediaNum = in.readInt();
		genre = in.readString();
		playtimems = in.readString();
		int foldertype = in.readInt();
		setFoldertype(foldertype);
		item_index = in.readInt();	
	}	

	public static final Parcelable.Creator<MediaItemData> CREATOR =
		new Parcelable.Creator<MediaItemData>() {
		public MediaItemData createFromParcel(Parcel in) {
			return new MediaItemData(in);
		}
		public MediaItemData[] newArray(int size) {
			return new MediaItemData[size];
		}
	};

	private MediaItemData(Parcel in) {
		readFromParcel(in);
	}
}
