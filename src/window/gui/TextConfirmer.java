package window.gui;

public interface TextConfirmer {

	boolean confirm(String oldString, String newString);

	class RegexConfirmer implements TextConfirmer {
		private String regex;
		public RegexConfirmer(String regex) {
			this.regex = regex;
		}

		@Override
		public boolean confirm(String oldString, String newString) {
			return newString.matches(this.regex);
		}
	}

	class NumberConfirme extends RegexConfirmer {
		public NumberConfirme() {
			super("[0-9]*");
		}
	}

	class WordConfirmer extends RegexConfirmer {
		public WordConfirmer() {
			super("[A-z]*");
		}
	}

	class AllConfirmer implements TextConfirmer {

		@Override
		public boolean confirm(String oldString, String newString) {
			return true;
		}
	}

}
