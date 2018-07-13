/*
 * Autopsy Forensic Browser
 *
 * Copyright 2018 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.contentviewers;


import com.google.common.base.Strings;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.corecomponentinterfaces.DataContentViewer;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.datamodel.AbstractFile;

/**
 * Content viewer that incorporates file type-specific viewers, such as ones
 * for pictures, video, etc.
 */
@ServiceProvider(service = DataContentViewer.class, position = 3)
@SuppressWarnings("PMD.SingularField") // UI widgets cause lots of false positives
public class FileViewer extends javax.swing.JPanel implements DataContentViewer {

    private static final int CONFIDENCE_LEVEL = 5;
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FileViewer.class.getName());

    private final Map<String, FileTypeViewer> mimeTypeToViewerMap = new HashMap<>();

    // TBD: This hardcoded list of viewers should be replaced with a dynamic lookup
    private static final FileTypeViewer[] KNOWN_VIEWERS = new FileTypeViewer[]{
        new SQLiteViewer(),
        new PListViewer(),
        new MediaFileViewer()
    };

    private FileTypeViewer lastViewer;

    /**
     * Creates new form ApplicationContentViewer
     */
    public FileViewer() {

        // init the mimetype to viewer map
        for (FileTypeViewer cv : KNOWN_VIEWERS) {
            cv.getSupportedMIMETypes().forEach((mimeType) -> {
                if (mimeTypeToViewerMap.containsKey(mimeType) == false) {
                    mimeTypeToViewerMap.put(mimeType, cv);
                } else {
                    LOGGER.log(Level.WARNING, "Duplicate viewer for mimtype: {0}", mimeType); //NON-NLS
                }
            });
        }

        initComponents();

        //LOGGER.log(Level.INFO, "Created ApplicationContentViewer instance: {0}", this); //NON-NLS
    }

    /**
     * Get the FileTypeViewer for a given mimetype
     *
     * @param mimeType
     *
     * @return FileTypeViewer, null if no known content viewer supports the mimetype
     */
    private FileTypeViewer getSupportingViewer(String mimeType) {
        return  mimeTypeToViewerMap.get(mimeType);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.OverlayLayout(this));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void setNode(Node selectedNode) {
        
        resetComponent();
        
        if (selectedNode == null) {
            return;
        }

        AbstractFile file = selectedNode.getLookup().lookup(AbstractFile.class);
        if (file == null) {
            return;
        }

        String mimeType = file.getMIMEType();
        if (Strings.isNullOrEmpty(mimeType)) {
            // LOGGER.log(Level.INFO, "Mimetype not known for file: {0}", file.getName()); //NON-NLS
            try {
                FileTypeDetector fileTypeDetector = new FileTypeDetector();
                mimeType = fileTypeDetector.getMIMEType(file);
            }catch (FileTypeDetector.FileTypeDetectorInitException ex) {
                LOGGER.log(Level.SEVERE, "Failed to initialize FileTypeDetector.", ex); //NON-NLS
                return;
            }
        }
        
        if (mimeType.equalsIgnoreCase("application/octet-stream")) {
            return;
        } 

        FileTypeViewer viewer = getSupportingViewer(mimeType);
        if (viewer != null) {
            lastViewer = viewer;

            viewer.setFile(file);
            this.removeAll();
            this.add(viewer.getComponent());
            this.repaint();
        }       
    }

    @Override
    @NbBundle.Messages("ApplicationContentViewer.title=Application")
    public String getTitle() {
        return Bundle.ApplicationContentViewer_title();
    }

    @Override
    @NbBundle.Messages("ApplicationContentViewer.toolTip=Displays file contents.")
    public String getToolTip() {
        return Bundle.ApplicationContentViewer_toolTip();
    }

    @Override
    public DataContentViewer createInstance() {
        return new FileViewer();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void resetComponent() {

        if (lastViewer != null) {
            lastViewer.resetComponent();
        }
        this.removeAll();
        lastViewer = null;
    }

    @Override
    public boolean isSupported(Node node) {

        if (node == null) {
            return false;
        }

        AbstractFile aFile = node.getLookup().lookup(AbstractFile.class);
        if (aFile == null) {
            return false;
        }

        String mimeType = aFile.getMIMEType();
        if (Strings.isNullOrEmpty(mimeType)) {
            LOGGER.log(Level.INFO, "Mimetype not known for file: {0}", aFile.getName()); //NON-NLS
            try {
                FileTypeDetector fileTypeDetector = new FileTypeDetector();
                mimeType = fileTypeDetector.getMIMEType(aFile);
            }catch (FileTypeDetector.FileTypeDetectorInitException ex) {
                LOGGER.log(Level.SEVERE, "Failed to initialize FileTypeDetector.", ex); //NON-NLS
                return false;
            }
        }
        
        if (mimeType.equalsIgnoreCase("application/octet-stream")) {
            return false;
        } else {
            return (getSupportingViewer(mimeType) != null);
        }
       
    }

    @Override
    public int isPreferred(Node node) {
        AbstractFile file = node.getLookup().lookup(AbstractFile.class);
        String mimeType = file.getMIMEType();
        
        if (Strings.isNullOrEmpty(mimeType)) {
            LOGGER.log(Level.INFO, "Mimetype not known for file: {0}", file.getName()); //NON-NLS
            try {
                FileTypeDetector fileTypeDetector = new FileTypeDetector();
                mimeType = fileTypeDetector.getMIMEType(file);
            }catch (FileTypeDetector.FileTypeDetectorInitException ex) {
                LOGGER.log(Level.SEVERE, "Failed to initialize FileTypeDetector.", ex); //NON-NLS
                return 0;
            }
        }
        
        if (mimeType.equalsIgnoreCase("application/octet-stream")) {
            return 0;
        } else {
            if (null != getSupportingViewer(mimeType)) {
                return CONFIDENCE_LEVEL;
            }
        }
        
        return 0;
    }
}
