package ncsa.d2k.modules.core.io.file.gui;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Allow the user to input a single file name.
 */
public class Input1FileName extends UIModule {

	private String lastFile;
	private String defaultPath;

	public void setLastFile(String s) {
        lastFile = s;
	}

	public String getLastFile() {
		return lastFile;
	}

	public void setDefaultPath(String s) {
		defaultPath = s;
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public String[] getInputTypes() {
		return null;
	}

	public String getInputInfo(int i) {
		return "";
	}

	public String[] getOutputTypes() {
		String[] out = {"java.lang.String"};
		return out;
	}

	public String getOutputInfo(int i) {
		return "The absolute pathname of the selected file.";
	}

	public String getModuleInfo() {
		return "Select a file from the local file system.  Properties: "+
            "defaultPath: the default directory that is shown in the file chooser. "+
            "lastFile: the last file that was chosen. This is simply used to save "+
            "state and should not be edited.";
	}

	public void beginExecution() {
		super.beginExecution();

		// do stuff with the view here
        if(view != null)
            view.doSetup();
	}

	private FileView view = null;

	protected UserView createUserView() {
		view = new FileView();
		return view;
	}

	public String[] getFieldNameMapping() {
		return null;
	}

    /**
     * The user view is a file chooser.
     */
	private class FileView extends JFileChooser implements UserView {

        private FileView() {
            setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }

		public void initView(ViewModule m) {
		}

        void doSetup() {
            String lastpath = getLastFile();
            if(lastpath != null)
                setSelectedFile(new File(lastpath));
            else {
                String startdir = getDefaultPath();
                if(startdir != null)
                    setCurrentDirectory(new File(startdir));
            }
        }

        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, y, w, h);
            doSetup();
        }

        public void approveSelection() {
            String filename = getSelectedFile().getAbsolutePath();
            setLastFile(filename);
            pushOutput(filename, 0);
            viewDone("Done");
        }

        public void cancelSelection() {
            viewAbort();
        }

        public Object getMenu() {
            return null;
        }

        public void compileResults(Hashtable ht, Component[] c) {
        }

		public void setInput(Object o, int i) {
			// no inputs, so this is never called
		}
	}
}
