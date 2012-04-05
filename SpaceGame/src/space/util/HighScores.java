package space.util;

import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import space.state.SpaceGameMain;

public class HighScores {

	static Preferences prefs = Preferences.userNodeForPackage(SpaceGameMain.class);
	
	private static ArrayList<Score> rank = new ArrayList<Score>();
	private static String str = "no rankings";
	
	static class Score {
		String name;
		int score;
		int wave;
		int upgrades;
		Score(String name, int wave, int score, int upgrades) {
			this.name = name;
			this.score = score;
			this.wave = wave;
			this.upgrades = upgrades;
		}
	}
	
	 
	public static void place(String name, int wave, int score, int upgrades) {
		place(new Score(name, wave, score, upgrades));
	}
	
	public static void place(Score score) {
		boolean added = false;
		score.name = score.name.replace(':', ' ').replace('\n', ' ');
		for (int i=0; i<rank.size(); i++) {
			Score s = rank.get(i);
			if (isBetter(score, s)) {
				rank.add(i, score);
				added = true;
				break;
			}
		}
		if (!added)
			rank.add(score);
		updateStr();
	}
	
	private static boolean isBetter(Score s, Score other) {
		if (s.wave > other.wave) {
			return true;
		} else if (s.wave == other.wave) {
			if (s.score > s.score) {
				return true;
			} else if (s.score == other.score) {
				if (s.upgrades < other.upgrades) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static void updateStr() {
		StringBuilder b = new StringBuilder();
		for (int i=0; i<10 && i<rank.size(); i++) {
			Score s = rank.get(i);
			b.append("wave ");
			b.append(s.wave);
			b.append(" - ");
			b.append(s.name);
			b.append(" - ");
			b.append(s.score);
			b.append(" pts - ");
			b.append(s.upgrades);
			b.append(" upgrades");
			b.append("\n");
		}
		str = b.toString();
	}
	
	public static void load() {
		for (int i=0; i<10; i++) {
			String line = prefs.get("highscore"+i, null);
			if (line==null)
				break;
			Score s = unpack(line.trim());
			place(s.name, s.wave, s.score, s.upgrades);			
		}
	}
	
	public static void store() {
		for (int i=0; i<rank.size() && i<10; i++) {
			prefs.put("highscore"+i, pack(rank.get(i)));
		}
	}
	
	public static String pack(Score s) {
		return s.name+":"+s.wave+":"+s.score+":"+s.upgrades;
	}
	
	public static Score unpack(String s) {
		if (s==null||s.trim().length()==0)
			return null;
		String[] a = s.split(":");
		if (a==null||a.length<4)
			return null;
		try {
			int w = Integer.parseInt(a[1]);
			int sc = Integer.parseInt(a[2]);
			int u = Integer.parseInt(a[3]);
			return new Score(a[0], w, sc, u);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static String list() {
		return str;
	}
}
