package co.theengine.kaltura;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.robotium.solo.Solo;

@SuppressWarnings("unchecked")
public class TestPlayer extends ActivityInstrumentationTestCase2<Activity> {
	private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.example.videoplayer.VideoPlayerActivity";
	private static Class launcherActivityClass;
	
	/**
	 * Retrieve the tested application class.
	 */
	static {
		try {
			launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public TestPlayer() throws ClassNotFoundException {
		super(launcherActivityClass);
	}

	private Solo solo;
	
	/**
	 * Incremental counter for screenshot files.
	 * Used with the alternative
	 */
	//private int screenshotCounter = 0;

	/**
	 * Initialize the test.
	 */
	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	/**
	 * Take a screenshot and save it with a sequential file name.
	 */
	private void screenshot() {
		solo.takeScreenshot();
		// Alternative way to screenshot (doesn't work for Robotium apparently)
		/*
		File f;
		try {
			File dir = new File("/sdcard/Robotium-Screenshots/");
			dir.mkdirs();
			
			String id = String.format("%04d", screenshotCounter++);
			f = new File(dir, "screenshot-"+id+".png");
			System.out.println("Screenshot path " + f);
			// takeScreenshot doesn't seem to be that reliable across devices/emulators
			//Boolean success = getUiDevice().takeScreenshot(f);
			//assertTrue("Unable to take a screenshot", success);
			
			System.out.println("SCREENSHOTTING!!!! " + "screencap -p " + f.getAbsolutePath());
			
			Process process = Runtime.getRuntime().exec("screencap -p " + f.getAbsolutePath());
			try {
				System.out.println("Waiting...");

				System.out.println("OUTPUT: "+IOUtils.toString(process.getInputStream()));
				System.out.println("ERRORS: "+IOUtils.toString(process.getErrorStream()));
				
				process.waitFor();
				
				System.out.println("EXIT: "+process.exitValue());
				
				System.out.println("Done!");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Interrupted! " + e);
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("IOException! " + e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//*/
	}

	/**
	 * Sleep for the provided amount of milliseconds.
	 */
	private void sleep(int time) {
		solo.sleep(time);
	}
	
	/**
	 * Toggle pause state on the stream.
	 */
	private void togglePause() {
		solo.clickOnMenuItem("Play/Pause");
	}
	
	/**
	 * Play the stream from the specified menu item.
	 */
	private void playStream(String itemStream) {
		solo.clickOnMenuItem("Streams");
		sleep(500);
		solo.clickOnMenuItem(itemStream, true);
	}
	
	/**
	 * Test the provided stream menu option by just playing it for a while.
	 * @param itemStream	Menu option name to test.
	 */
	private void playTest(String itemStream) {
		playStream(itemStream);

		screenshot();
		for (int i = 0; i < 4; i++) {
			sleep(15000);
			screenshot();
		}
		
		int time = getTime();
		assertTrue(time > 1000);
	}
	
	/**
	 * Grab the current time in milliseconds from the displayed debug text.
	 */
	private int getTime() {
		final TextView debug = (TextView) solo.getView("subTitleView");
		String s = debug.getText().toString();
		String timeStr = "Time: ";
		int timeIndex = s.indexOf(timeStr);
		if (timeIndex == -1) return -1;
		int newlineIndex = s.indexOf("\n", timeIndex);
		assertTrue(newlineIndex != -1);
		return Integer.valueOf(s.substring(timeIndex+timeStr.length(), newlineIndex));
	}
	
	/**
	 * Test the provided stream menu option with screenshots and pause testing.
	 * @param itemStream	Menu option name to test.
	 */
	private void streamTest(String itemStream) {
		
		screenshot();
		
		playStream(itemStream);
		
		for (int i = 0; i < 5; i++) {
			sleep(5000);
			screenshot();
		}
		
		togglePause();
		
		screenshot();
		sleep(2500);

		screenshot();
		sleep(2500);

		screenshot();
		
		togglePause();

		screenshot();
		sleep(2500);
		
		screenshot();
		sleep(2500);
		
		screenshot();
		sleep(5000);
		
		screenshot();
	}

	/**
	 * Test seeking forward and backward on the provided stream menu item.
	 */
	private void seekTest(String itemStream) {
		playStream(itemStream);

		screenshot();
		sleep(3000);
		screenshot();

		solo.clickOnMenuItem("Seek Fwd");

		screenshot();
		sleep(2000);
		screenshot();
		
		solo.clickOnMenuItem("Seek Fwd");

		screenshot();
		sleep(2000);
		screenshot();
		
		solo.clickOnMenuItem("Seek Backward");

		screenshot();
		sleep(4000);
		screenshot();
		
		solo.clickOnMenuItem("Seek Backward");

		screenshot();
		sleep(4000);
		screenshot();
		
		solo.clickOnMenuItem("Seek Fwd");

		screenshot();
		sleep(2000);
		screenshot();
	}

	/**
	 * Test the playback of Kaltura video on demand via the menu option.
	 */
	public void testPlayKalturaVOD() {
		playTest("Kaltura VoD");
	}
	
	/**
	 * Test the ABC live stream via the menu option.
	 */
	public void testABC() {
		streamTest("ABC DVR");
	}

	/**
	 * Test the Kaltura video on demand via the menu option.
	 */
	public void testKalturaVOD() {
		streamTest("Kaltura VoD");
	}

	/**
	 * Test the Folgers stream.
	 */
	public void testFolgers() {
		streamTest("Folgers");
	}

	/**
	 * Test the alternate audio VOD stream.
	 */
	public void testVODAltAudio() {
		streamTest("VOD Alt Audio");
	}
	
	public void testBibbop() {
		streamTest("Bibbop");
	}

	public void testAESVOD() {
		streamTest("AES VOD");
	}

	/**
	 * Test playing a stream again after playing it the first time.
	 */
	public void testPlayTwice() {
		playStream("ABC DVR");

		screenshot();
		sleep(2500);
		screenshot();
		sleep(2500);
		screenshot();

		playStream("ABC DVR");

		screenshot();
		sleep(2500);
		screenshot();
		sleep(2500);
		screenshot();
	}

	/**
	 * Test switching between ABC and Kaltura streams.
	 */
	public void testSwitch() {
		playStream("ABC DVR");

		screenshot();
		sleep(5000);
		screenshot();
		
		playStream("Kaltura VoD");

		screenshot();
		sleep(5000);
		screenshot();
		
		playStream("ABC DVR");
		
		screenshot();
		sleep(5000);
		screenshot();
		
		playStream("Kaltura VoD");
		
		screenshot();
		sleep(5000);
		screenshot();
	}

	/**
	 * Test playing a stream after pausing the previous one.
	 */
	public void testPlayAfterPause() {
		playStream("Kaltura VoD");

		screenshot();
		sleep(5000);
		screenshot();
		
		togglePause();
		
		screenshot();
		sleep(3000);
		screenshot();
		
		playStream("Folgers");

		screenshot();
		sleep(5000);
		screenshot();
	}
	
	/**
	 * Test seeking forward and backward on the ABC DVR stream.
	 */
	public void testABCSeek() {
		seekTest("ABC DVR");
	}

	/**
	 * Test seeking forward and backward on the Kaltura stream.
	 */
	public void testKalturaVODSeek() {
		seekTest("Kaltura VoD");
	}
	
	public void testFolgersSeek() {
		seekTest("Folgers");
	}

	public void testVODAltAudioSeek() {
		seekTest("VOD Alt Audio");
	}
	
	public void testBibbopSeek() {
		seekTest("Bibbop");
	}
	
	public void testAESVODSeek() {
		seekTest("AES VOD");
	}
	
	private void streamRunTest(String itemStream) {
		screenshot();
		playStream(itemStream);
		screenshot();
		sleep(2000);
		screenshot();
		sleep(2000);
		screenshot();
		sleep(2000);
		screenshot();
	}

	/**
	 * Test a stream in the portrait orientation.
	 */
	public void testOrientationPortrait() {
		solo.setActivityOrientation(Solo.PORTRAIT);
		sleep(1000);
		streamRunTest("ABC DVR");
	}

	/**
	 * Test a stream in the landscape orientation.
	 */
	public void testOrientationLandscape() {
		solo.setActivityOrientation(Solo.LANDSCAPE);
		sleep(1000);
		streamRunTest("ABC DVR");
	}

	/**
	 * Test a stream with orientation changing mid-stream.
	 */
	public void testOrientationChange() {
		solo.setActivityOrientation(Solo.PORTRAIT);
		sleep(1000);
		screenshot();
		
		playStream("ABC DVR");
		screenshot();
		
		sleep(2000);
		screenshot();
		sleep(2000);
		screenshot();

		solo.setActivityOrientation(Solo.LANDSCAPE);
		
		screenshot();
		sleep(1000);
		
		screenshot();
		sleep(2000);
		
		screenshot();
		sleep(2000);
		
		screenshot();
		solo.setActivityOrientation(Solo.PORTRAIT);

		screenshot();
		sleep(1000);
		
		screenshot();
		sleep(2000);
		
		screenshot();
		sleep(2000);
		
		screenshot();
	}
	
	/**
	 * Finish the test.
	 */
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}