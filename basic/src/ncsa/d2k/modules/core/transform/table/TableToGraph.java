package ncsa.d2k.modules.core.transform.table;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.pgraph.nodes.*;

/**
 * <p>Overview: This module takes a <i>Table</i> and creates a graph (as
 * defined by the JUNG package) with vertices and edges.</p><p>Acknowledgement:
 * This module uses functionality from the JUNG project. See
 * http://jung.sourceforge.net.</p>
 */
public class TableToGraph extends DataPrepModule {

    /**
     * Performs the main work of the module.
     *
     * @throws Exception if a problem occurs while performing the work of the module
     */
    public void doit() throws Exception {

        Table table = (Table) pullInput(0);
        int table_rows = table.getNumRows();

        Graph graph = new UndirectedSparseGraph();

        Double vertexSize = new Double(8.0);
        Double borderWidth = new Double(0.5);
        Integer squareInt = new Integer(PVertexNode.SQUARE);
        Integer circleInt = new Integer(PVertexNode.CIRCLE);
        Integer labelMax = new Integer(12);
        String fontMono = "Monospaced";
        Color edgeColor = Color.decode("#00CC00");
        Double edgeWidth = new Double(1.5);
        Integer edgeLabelSize = new Integer(10);

        HashMap vertexMap = new HashMap();
        int id = 0;

        String label;
        Vertex v;
        for (int row = 0; row < table_rows; row++) {

            label = stripInvalidXML(table.getString(row, prop_source_attr));

            v = (Vertex) vertexMap.get(label);
            if (v == null) {

                v = graph.addVertex(new UndirectedSparseVertex());

                v.setUserDatum(PVertexNode.LABEL, label, UserData.SHARED);
                v.setUserDatum(PVertexNode.LABELMAX, labelMax, UserData.SHARED);
                v.setUserDatum("id", Integer.toString(id++), UserData.SHARED);

                v.setUserDatum(PVertexNode.COLOR, Color.RED, UserData.SHARED);
                v.setUserDatum(PVertexNode.SHAPE, circleInt, UserData.SHARED);
                v.setUserDatum(PVertexNode.SIZE, vertexSize, UserData.SHARED);
                v.setUserDatum(PVertexNode.BORDERCOLOR, Color.LIGHT_GRAY, UserData.SHARED);
                v.setUserDatum(PVertexNode.BORDERWIDTH, borderWidth, UserData.SHARED);

                vertexMap.put(label, v);

            }

        }

        for (int row = 0; row < table_rows; row++) {

            label = stripInvalidXML(table.getString(row, prop_target_attr));

            v = (Vertex) vertexMap.get(label);
            if (v == null) {

                v = graph.addVertex(new UndirectedSparseVertex());

                v.setUserDatum(PVertexNode.LABEL, label, UserData.SHARED);
                v.setUserDatum(PVertexNode.LABELMAX, labelMax, UserData.SHARED);
                // v.setUserDatum("id", Integer.toString(id++), UserData.SHARED);

                v.setUserDatum(PVertexNode.COLOR, Color.BLUE, UserData.SHARED);
                v.setUserDatum(PVertexNode.SHAPE, squareInt, UserData.SHARED);
                v.setUserDatum(PVertexNode.SIZE, vertexSize, UserData.SHARED);
                v.setUserDatum(PVertexNode.BORDERCOLOR, Color.DARK_GRAY, UserData.SHARED);
                v.setUserDatum(PVertexNode.BORDERWIDTH, borderWidth, UserData.SHARED);
                v.setUserDatum(PVertexNode.LABELFONTNAME, fontMono, UserData.SHARED);

                vertexMap.put(label, v);

            }

        }

        Iterator eIter;
        Edge e;
        Pair p;
        Vertex v1, v2;
        boolean duplicate;
        for (int row = 0; row < table_rows; row++) {

            v1 = (Vertex) vertexMap.get(stripInvalidXML(table.getString(row, prop_source_attr)));
            v2 = (Vertex) vertexMap.get(stripInvalidXML(table.getString(row, prop_target_attr)));

            eIter = graph.getEdges().iterator();
            duplicate = false;
            while (eIter.hasNext()) {

                e = (Edge) eIter.next();
                p = e.getEndpoints();
                if (p.getFirst() == v1 && p.getSecond() == v2 ||
                        p.getFirst() == v2 && p.getSecond() == v1) {
                    duplicate = true;
                    break;
                }

            }

            if (!duplicate) {

                e = graph.addEdge(new UndirectedSparseEdge(v1, v2));

                if (prop_edge_label_attr >= 0) {
                    e.setUserDatum(PEdgeNode.LABEL,
                            stripInvalidXML(table.getString(row, prop_edge_label_attr)),
                            UserData.SHARED);
                }

                e.setUserDatum(PEdgeNode.COLOR, edgeColor, UserData.SHARED);
                e.setUserDatum(PEdgeNode.WIDTH, edgeWidth, UserData.SHARED);
                e.setUserDatum(PEdgeNode.LABELSIZE, edgeLabelSize, UserData.SHARED);
                e.setUserDatum(PEdgeNode.LABELSTYLE, PEdgeNode.STYLE_ITALIC, UserData.SHARED);

            }

        }

        pushOutput(graph, 0);
    }

    /**
     * Strip all &, < , > , ' , and \ from str and replace with _ (underscore)
     * @param str string
     * @return  string with all &, <, >, ', and \ converted to _ (underscore)
     */
   private String stripInvalidXML(String str) {

      String empty = "_";

      str = str.replaceAll("&", empty);
      str = str.replaceAll("<", empty);
      str = str.replaceAll(">", empty);
      str = str.replaceAll("'", empty);
      str = str.replaceAll("\"", empty);

      return str;
   }

    /**
     * Returns a description of the input at the specified index.
     *
     * @param index Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int index) {

        if (index == 0) {
            return "A <i>Table</i> in which two attributes can be identified as " +
                    "nodes in a graph and each row defines the relationships " +
                    "between these attributes.";
        } else {
            return null;
        }

    }


    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String[] getInputTypes() {
        return new String[]{"ncsa.d2k.modules.core.datatype.Table"};
    }

    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        return "<p>Overview: This module takes a <i>Table</i> and creates a graph (as " +
                "defined by the JUNG package) with vertices and edges.</p><p>Acknowledgement: " +
                "This module uses functionality from the JUNG project. See " +
                "http://jung.sourceforge.net.</p>";
    }

    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Table to Graph";
    }

    /**
     * Returns the name of the input at the specified index.
     *
     * @param index Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int index) {

        if (index == 0) {
            return "Table";
        } else {
            return null;
        }

    }

    /**
     * Returns a description of the output at the specified index.
     *
     * @param index Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int index) {

        if (index == 0) {
            return "A graph (as defined by the JUNG package). It initializes " +
                    "vertices and edges with <i>user data</i> that specify a " +
                    "default visual representation.";
        } else {
            return null;
        }

    }

    /**
     * Returns the name of the output at the specified index.
     *
     * @param index Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int index) {

        if (index == 0) {
            return "Graph";
        } else {
            return null;
        }

    }

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String[] getOutputTypes() {
        return new String[]{"edu.uci.ics.jung.graph.Graph"};
    }

   /////////////////////////////////////////////////////////////////////////////
   // properties

    /** the index of that attribute in the <i>Table</i> which specifies
            the label of the connecting edge (optional; set to -1 to disable */
   private int prop_edge_label_attr = 2;
    /** the index of that attribute in the <i>Table</i> which specifies
            "the source vertex */
   private int prop_source_attr = 0;
    /** the index of that attribute in the <i>Table</i> which specifies
            the target vertex */
   private int prop_target_attr = 1;

    /**
     * Get edge label attribute
     * @return edge label attribute
     */
   public int getEdgeLabelAttr() { return prop_edge_label_attr; }

    /**
     * Get source attribute
     * @return source attribute
     */
   public int getSourceAttr() { return prop_source_attr; }

    /**
     * Get target attribute
     * @return target attribute
     */
   public int getTargetAttr() { return prop_target_attr; }

    /**
     * Set edge label attribute
     * @param value new edge label attribute
     */
   public void setEdgeLabelAttr(int value) { prop_edge_label_attr = value; }

    /**
     * Set source attribute
     * @param value new source attribute
     */
   public void setSourceAttr(int value) { prop_source_attr = value; }

    /**
     * Set target attribute
     * @param value new target attribute
     */
   public void setTargetAttr(int value) { prop_target_attr = value; }

    /**
     * The property descriptions
     */
   private static PropertyDescription[] pds = new PropertyDescription[] {
      new PropertyDescription(
         "sourceAttr",
         "Source Vertex Attribute",
         "the index of that attribute in the <i>Table</i> which specifies " +
            "the source vertex"
      ),
      new PropertyDescription(
         "targetAttr",
         "Target Vertex Attribute",
         "the index of that attribute in the <i>Table</i> which specifies " +
            "the target vertex"
      ),
      new PropertyDescription(
         "edgeLabelAttr",
         "Edge Label Attribute",
         "the index of that attribute in the <i>Table</i> which specifies " +
            "the label of the connecting edge (optional; set to -1 to disable)"
      )
   };

    /**
     * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the
     * module.
     *
     * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        return pds;
    }

    /**
     * Return the custom property editor for this module.
     *
     * @return custom property editor
     */
    public CustomModuleEditor getPropertyEditor() {
        return new PropertyEditor();
    }

   /////////////////////////////////////////////////////////////////////////////
   // custom property editor

    /**
     * A custom property editor for this module.
     */
   private class PropertyEditor extends JPanel implements ActionListener,
      CustomModuleEditor {

        /** insets */
      private Insets emptyInsets = new Insets(1, 1, 1, 1);
        /** checkbox for edge label */
      private JCheckBox edgeLblCheck;
        /** field to enter edge label column index */
      private JTextField edgeLblField;
        /** field to enter source column index */
      private JTextField sourceField;
        /** field to enter target field index */
      private JTextField targetField;

        /**
         * Constructor
         */
      PropertyEditor() {

         super();

         JLabel sourceLabel = new JLabel("Source vertex attribute");
         sourceLabel.setHorizontalAlignment(JLabel.LEFT);
         JLabel targetLabel = new JLabel("Target vertex attribute");
         targetLabel.setHorizontalAlignment(JLabel.LEFT);
         edgeLblCheck = new JCheckBox("Edge label attribute", true);
         edgeLblCheck.setHorizontalAlignment(JCheckBox.LEFT);
         edgeLblCheck.addActionListener(this);
         edgeLblField = new JTextField(Integer.toString(prop_edge_label_attr), 3);
         sourceField = new JTextField(Integer.toString(prop_source_attr), 3);
         targetField = new JTextField(Integer.toString(prop_target_attr), 3);

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         layout.addLayoutComponent(sourceLabel, new GridBagConstraints(
            0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(sourceField, new GridBagConstraints(
            1, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(targetLabel, new GridBagConstraints(
            0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(targetField, new GridBagConstraints(
            1, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(edgeLblCheck, new GridBagConstraints(
            0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(edgeLblField, new GridBagConstraints(
            1, 2, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));

         add(sourceLabel);
         add(sourceField);
         add(targetLabel);
         add(targetField);
         add(edgeLblCheck);
         add(edgeLblField);

      }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {

            Object source = e.getSource();

            if (source == edgeLblCheck) {

                edgeLblField.setEditable(edgeLblCheck.isSelected());
                edgeLblField.setEnabled(edgeLblCheck.isSelected());

            }

        }

        /**
         * This method is called when the property sheet is closed and the properties
         * should be committed.  This method will set the changed values in the module. If the
         * properties can not be set for any reason, return false
         *
         * @return true if any parameters actually changed, otherwise return false.
         * @throws PropertyVetoException if for any reason, the parameters can not be set, throw an
         *                   exception with an appropriate message.
         */
        public boolean updateModule() throws PropertyVetoException {

            int user_source = 0, user_target = 0, user_edge_label = 0;

            try {
                user_source = Integer.parseInt(sourceField.getText());
            }
            catch (NumberFormatException e) {
                veto("source: not an integer");
            }
            if (user_source < 0) {
                veto("source attribute < 0");
            }

            try {
                user_target = Integer.parseInt(targetField.getText());
            }
            catch (NumberFormatException e) {
                veto("target: not an integer");
            }
            if (user_target < 0) {
                veto("target attribute < 0");
            }

            if (edgeLblCheck.isSelected()) {

                try {
                    user_edge_label = Integer.parseInt(edgeLblField.getText());
                }
                catch (NumberFormatException e) {
                    veto("edge label: not an integer");
                }
                if (user_edge_label < 0) {
                    veto("edge label attribute < 0");
                }

            } else {
                user_edge_label = -1;
            }

            boolean changed = false;

            if (prop_source_attr != user_source) {
                prop_source_attr = user_source;
                changed = true;
            }
            if (prop_target_attr != user_target) {
                prop_target_attr = user_target;
                changed = true;
            }
            if (prop_edge_label_attr != user_edge_label) {
                prop_edge_label_attr = user_edge_label;
                changed = true;
            }

            return changed;

        }

        /**
         * create a new property veto exception and throw it
         * @param reason the reason
         * @throws PropertyVetoException the new property veto exception
         */
      private void veto(String reason) throws PropertyVetoException {
         throw new PropertyVetoException(reason, null);
      }

   }

}
