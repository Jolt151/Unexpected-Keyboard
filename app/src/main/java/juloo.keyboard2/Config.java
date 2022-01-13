package juloo.keyboard2;

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import juloo.keyboard2.R;

final class Config
{
  // From resources
  public final float marginTop;
  public final float keyPadding;
  public final float keyVerticalInterval;
  public final float keyHorizontalInterval;

  // From preferences
  public int layout; // Or '-1' for the system defaults
  private float swipe_dist_dp;
  public float swipe_dist_px;
  public boolean vibrateEnabled;
  public long vibrateDuration;
  public long longPressTimeout;
  public long longPressInterval;
  public float marginBottom;
  public float keyHeight;
  public float horizontalMargin;
  public boolean preciseRepeat;
  public float characterSize; // Ratio
  public int accents; // Values are R.values.pref_accents_v_*
  public int theme; // Values are R.style.*

  // Dynamically set
  public boolean shouldOfferSwitchingToNextInputMethod;
  public int key_flags_to_remove;
  public String actionLabel; // Might be 'null'
  public int actionId; // Meaningful only when 'actionLabel' isn't 'null'
  public boolean swapEnterActionKey; // Swap the "enter" and "action" keys

  public final IKeyEventHandler handler;

  private Config(Context context, IKeyEventHandler h)
  {
    Resources res = context.getResources();
    // static values
    marginTop = res.getDimension(R.dimen.margin_top);
    keyPadding = res.getDimension(R.dimen.key_padding);
    keyVerticalInterval = res.getDimension(R.dimen.key_vertical_interval);
    keyHorizontalInterval = res.getDimension(R.dimen.key_horizontal_interval);
    // default values
    layout = -1;
    vibrateEnabled = true;
    vibrateDuration = 20;
    longPressTimeout = 600;
    longPressInterval = 65;
    marginBottom = res.getDimension(R.dimen.margin_bottom);
    keyHeight = res.getDimension(R.dimen.key_height);
    horizontalMargin = res.getDimension(R.dimen.horizontal_margin);
    preciseRepeat = true;
    characterSize = 1.f;
    accents = 1;
    // from prefs
    refresh(context);
    // initialized later
    shouldOfferSwitchingToNextInputMethod = false;
    key_flags_to_remove = 0;
    actionLabel = null;
    actionId = 0;
    swapEnterActionKey = false;
    handler = h;
  }

  /*
   ** Reload prefs
   */
  public void refresh(Context context)
  {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    layout = layoutId_of_string(prefs.getString("layout", "system")); 
    swipe_dist_dp = Float.valueOf(prefs.getString("swipe_dist", "15"));
    swipe_dist_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, swipe_dist_dp, dm);
    vibrateEnabled = prefs.getBoolean("vibrate_enabled", vibrateEnabled);
    vibrateDuration = prefs.getInt("vibrate_duration", (int)vibrateDuration);
    longPressTimeout = prefs.getInt("longpress_timeout", (int)longPressTimeout);
    longPressInterval = prefs.getInt("longpress_interval", (int)longPressInterval);
    marginBottom = getDipPref(dm, prefs, "margin_bottom", marginBottom);
    keyHeight = getDipPref(dm, prefs, "key_height", keyHeight);
    horizontalMargin = getDipPref(dm, prefs, "horizontal_margin", horizontalMargin);
    preciseRepeat = prefs.getBoolean("precise_repeat", preciseRepeat);
    characterSize = prefs.getFloat("character_size", characterSize); 
    accents = Integer.valueOf(prefs.getString("accents", "1"));
    theme = themeId_of_string(prefs.getString("theme", ""));
  }

  private float getDipPref(DisplayMetrics dm, SharedPreferences prefs, String pref_name, float def)
  {
    int value = prefs.getInt(pref_name, -1);
    if (value < 0)
      return (def);
    return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm));
  }

  public static int layoutId_of_string(String name)
  {
    switch (name)
    {
      case "azerty": return R.xml.azerty;
      case "qwerty": return R.xml.qwerty;
      case "system": default: return -1;
    }
  }

  /* Used for the accents option. */
  public static int extra_key_flag_of_name(String name)
  {
    switch (name)
    {
      case "grave": return KeyValue.FLAG_ACCENT1;
      case "aigu": return KeyValue.FLAG_ACCENT2;
      case "circonflexe": return KeyValue.FLAG_ACCENT3;
      case "tilde": return KeyValue.FLAG_ACCENT4;
      case "cedille": return KeyValue.FLAG_ACCENT5;
      case "trema": return KeyValue.FLAG_ACCENT6;
      case "ring": return KeyValue.FLAG_ACCENT_RING;
      case "szlig": return KeyValue.FLAG_LANG_SZLIG;
      default: throw new RuntimeException(name);
    }
  }

  public static int themeId_of_string(String name)
  {
    switch (name)
    {
      case "light": return R.style.Light;
      case "black": return R.style.Black;
      default: case "dark": return R.style.Dark;
    }
  }

  private static Config _globalConfig = null;

  public static void initGlobalConfig(Context context, IKeyEventHandler handler)
  {
    _globalConfig = new Config(context, handler);
  }

  public static Config globalConfig()
  {
    return _globalConfig;
  }

  public static interface IKeyEventHandler
  {
    public void handleKeyUp(KeyValue value, int flags);
  }
}