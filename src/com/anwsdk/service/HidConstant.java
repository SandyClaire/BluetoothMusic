package com.anwsdk.service;

public class HidConstant {
	// Mouse Move Range
	public static final int MOUSE_MOVE_MAX_OFFSET					= 2047;
	public static final int MOUSE_MOVE_MIN_OFFSET					= -2047;

	// Mouse Button Type ID
	public static final int MOUSE_TYPE_MOVE					= 0x00;
	public static final int MOUSE_TYPE_LEFT_BUTTON_DOWN		= 0x01;
	public static final int MOUSE_TYPE_RIGHT_BUTTON_DOWN	= 0x02;
	public static final int MOUSE_TYPE_CENTER_BUTTON_DOWN	= 0x04;
	public static final int MOUSE_TYPE_LEFT_BUTTON_UP		= 0x00;
	public static final int MOUSE_TYPE_RIGHT_BUTTON_UP		= 0x00;
	public static final int MOUSE_TYPE_CENTER_BUTTON_UP		= 0x00;

	// Keyboard Special Button ID	
	public static final int KEYBOARD_MOD_CONTROL_LEFT    = (1<<0);
	public static final int KEYBOARD_MOD_SHIFT_LEFT      = (1<<1);
	public static final int KEYBOARD_MOD_ALT_LEFT        = (1<<2);
	public static final int KEYBOARD_MOD_GUI_LEFT        = (1<<3);
	public static final int KEYBOARD_MOD_CONTROL_RIGHT   = (1<<4);
	public static final int KEYBOARD_MOD_SHIFT_RIGHT     = (1<<5);
	public static final int KEYBOARD_MOD_ALT_RIGHT       = (1<<6);
	public static final int KEYBOARD_MOD_GUI_RIGHT       = (1<<7);

	//public static final int KEYBOARD_KEY_Reserved        = 0x00;
	//public static final int KEYBOARD_KEY_ErrorRollOver   = 0x01;
	//public static final int KEYBOARD_KEY_POSTFail		   = 0x02;
	//public static final int KEYBOARD_KEY_ErrorUndefined  = 0x03;
	public static final int KEYBOARD_KEY_A                = 0x04;
	public static final int KEYBOARD_KEY_B                = 0x05;		
	public static final int KEYBOARD_KEY_C                = 0x06;
	public static final int KEYBOARD_KEY_D                = 0x07;
	public static final int KEYBOARD_KEY_E                = 0x08;
	public static final int KEYBOARD_KEY_F                = 0x09;
	public static final int KEYBOARD_KEY_G                = 0x0A;
	public static final int KEYBOARD_KEY_H                = 0x0B;
	public static final int KEYBOARD_KEY_I                = 0x0C;
	public static final int KEYBOARD_KEY_J                = 0x0D;
	public static final int KEYBOARD_KEY_K                = 0x0E;
	public static final int KEYBOARD_KEY_L                = 0x0F;
	public static final int KEYBOARD_KEY_M                = 0x10;
	public static final int KEYBOARD_KEY_N                = 0x11;
	public static final int KEYBOARD_KEY_O                = 0x12;
	public static final int KEYBOARD_KEY_P                = 0x13;
	public static final int KEYBOARD_KEY_Q                = 0x14;
	public static final int KEYBOARD_KEY_R                = 0x15;
	public static final int KEYBOARD_KEY_S                = 0x16;
	public static final int KEYBOARD_KEY_T                = 0x17;
	public static final int KEYBOARD_KEY_U                = 0x18;
	public static final int KEYBOARD_KEY_V                = 0x19;
	public static final int KEYBOARD_KEY_W                = 0x1A;
	public static final int KEYBOARD_KEY_X                = 0x1B;
	public static final int KEYBOARD_KEY_Y                = 0x1C;
	public static final int KEYBOARD_KEY_Z                = 0x1D;
	public static final int KEYBOARD_KEY_1				  = 0x1E;	 // !
	public static final int KEYBOARD_KEY_2				  = 0x1F;	 // @
	public static final int KEYBOARD_KEY_3				  = 0x20;	 // #
	public static final int KEYBOARD_KEY_4				  = 0x21;	 // $
	public static final int KEYBOARD_KEY_5				  = 0x22;	 // %
	public static final int KEYBOARD_KEY_6				  = 0x23;	 // ^
	public static final int KEYBOARD_KEY_7				  = 0x24;	 // &
	public static final int KEYBOARD_KEY_8				  = 0x25;	 // *
	public static final int KEYBOARD_KEY_9				  = 0x26;	 // (
	public static final int KEYBOARD_KEY_0				  = 0x27;	 // )
	public static final int KEYBOARD_KEY_Return           = 0x28;    // ENTER
	public static final int KEYBOARD_KEY_ESCAPE           = 0x29;
	public static final int KEYBOARD_KEY_DELETE           = 0x2A;    // Backspace
	public static final int KEYBOARD_KEY_Tab              = 0x2B;
	public static final int KEYBOARD_KEY_Spacebar         = 0x2C;
	public static final int KEYBOARD_KEY_Minus_Sign       = 0x2D;    // - and (underscore)
	public static final int KEYBOARD_KEY_Equal_Sign       = 0x2E;    // = and +
	public static final int KEYBOARD_KEY_Open_Bracket     = 0x2F;    // [ and {
	public static final int KEYBOARD_KEY_Close_Bracket    = 0x30;    // ] and }
	public static final int KEYBOARD_KEY_Backslash        = 0x31;    // \ and |
	public static final int KEYBOARD_KEY_Pound_Sign       = 0x32;    // Non-US # and ~
	public static final int KEYBOARD_KEY_Semicolon        = 0x33;    // ; and :
	public static final int KEYBOARD_KEY_Single_Quote     = 0x34;    // ' and "
	public static final int KEYBOARD_KEY_Grave_Accent     = 0x35;    // Tilde
	public static final int KEYBOARD_KEY_Comma            = 0x36;    // , and <
	public static final int KEYBOARD_KEY_Dot              = 0x37;    // . and >
	public static final int KEYBOARD_KEY_Slash            = 0x38;    // / and ?
	public static final int KEYBOARD_KEY_Caps_Lock        = 0x39;
	public static final int KEYBOARD_KEY_F1				  = 0x3A;
	public static final int KEYBOARD_KEY_F2				  = 0x3B;
	public static final int KEYBOARD_KEY_F3				  = 0x3C;
	public static final int KEYBOARD_KEY_F4				  = 0x3D;
	public static final int KEYBOARD_KEY_F5				  = 0x3E;
	public static final int KEYBOARD_KEY_F6				  = 0x3F;
	public static final int KEYBOARD_KEY_F7				  = 0x40;
	public static final int KEYBOARD_KEY_F8				  = 0x41;
	public static final int KEYBOARD_KEY_F9				  = 0x42;
	public static final int KEYBOARD_KEY_F10			  = 0x43;
	public static final int KEYBOARD_KEY_F11			  = 0x44;
	public static final int KEYBOARD_KEY_F12			  = 0x45;
	public static final int KEYBOARD_KEY_PrintScreen	  = 0x46;
	public static final int KEYBOARD_KEY_Scroll_Lock	  = 0x47;
	public static final int KEYBOARD_KEY_Pause			  = 0x48;
	public static final int KEYBOARD_KEY_Insert			  = 0x49;
	public static final int KEYBOARD_KEY_Home			  = 0x4A;
	public static final int KEYBOARD_KEY_PageUp			  = 0x4B;
	public static final int KEYBOARD_KEY_Delete_Forward	  = 0x4C;
	public static final int KEYBOARD_KEY_End			  = 0x4D;
	public static final int KEYBOARD_KEY_PageDown		  = 0x4E;
	public static final int KEYBOARD_KEY_RightArrow		  = 0x4F;
	public static final int KEYBOARD_KEY_LeftArrow		  = 0x50;
	public static final int KEYBOARD_KEY_DownArrow		  = 0x51;
	public static final int KEYBOARD_KEY_UpArrow		  = 0x52;
	public static final int KEYBOARD_KEY_Num_Lock		  = 0x53;    // Keypad Num Lock and Clear
	public static final int KEYBOARD_KEY_Num_Slash		  = 0x54;    // Keypad /
	public static final int KEYBOARD_KEY_Num_Star   	  = 0x55;    // Keypad *
	public static final int KEYBOARD_KEY_Num_Minus_Sign	  = 0x56;    // Keypad -
	public static final int KEYBOARD_KEY_Num_Plus_Sign	  = 0x57;    // Keypad +
	public static final int KEYBOARD_KEY_Num_ENTER		  = 0x58;    // Keypad ENTER
	public static final int KEYBOARD_KEY_Num_1		      = 0x59;    // Keypad 1 and End
	public static final int KEYBOARD_KEY_Num_2		      = 0x5A;    // Keypad 2 and Down Arrow
	public static final int KEYBOARD_KEY_Num_3			  = 0x5B;    // Keypad 3 and PageDn
	public static final int KEYBOARD_KEY_Num_4			  = 0x5C;    // Keypad 4 and Left Arrow
	public static final int KEYBOARD_KEY_Num_5		      = 0x5D;    // Keypad 5
	public static final int KEYBOARD_KEY_Num_6			  = 0x5E;    // Keypad 6 and Right Arrow
	public static final int KEYBOARD_KEY_Num_7			  = 0x5F;    // Keypad 7 and Home
	public static final int KEYBOARD_KEY_Num_8			  = 0x60;    // Keypad 8 and Up Arrow
	public static final int KEYBOARD_KEY_Num_9			  = 0x61;    // Keypad 9 and PageUp
	public static final int KEYBOARD_KEY_Num_0			  = 0x62;    // Keypad 0 and Insert
	public static final int KEYBOARD_KEY_Num_Dot		  = 0x63;    // Keypad . and Delete
	public static final int KEYBOARD_KEY_Num_Backslash	  = 0x64;    // Keyboard Non-US \ and |
	public static final int KEYBOARD_KEY_Num_Application  = 0x65;    // Keyboard Application
	
	public static final int CONSUMER_TYPE_UP	= 0x00;
	public static final int CONSUMER_TYPE_BACK	= 0x01;
	public static final int CONSUMER_TYPE_HOME	= 0x02;
	
}
