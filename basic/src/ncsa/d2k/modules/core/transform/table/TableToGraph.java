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

public class TableToGraph extends DataPrepModule {

   public void doit() {

      Table table = (Table)pullInput(0);
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

         v = (Vertex)vertexMap.get(label);
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

         v = (Vertex)vertexMap.get(label);
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

         v1 = (Vertex)vertexMap.get(stripInvalidXML(table.getString(row, prop_source_attr)));
         v2 = (Vertex)vertexMap.get(stripInvalidXML(table.getString(row, prop_target_attr)));

         eIter = graph.getEdges().iterator();
         duplicate = false;
         while (eIter.hasNext()) {

            e = (Edge)eIter.next();
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

   private String stripInvalidXML(String str) {

      String empty = "_";

      str = str.replaceAll("&", empty);
      str = str.replaceAll("<", empty);
      str = str.replaceAll(">", empty);
      str = str.replaceAll("'", empty);
      str = str.replaceAll("\"", empty);

      return str;

   }

   /////////////////////////////////////////////////////////////////////////////
   // module info

   public String getInputInfo(int index) {

      if (index == 0) {
         return "A <i>Table</i> in which two attributes can be identified as " +
                "nodes in a graph and each row defines the relationships " +
                "between these attributes.";
      }
      else {
         return null;
      }

   }


   public String[] getInputTypes() {
      return new String[] {"ncsa.d2k.modules.core.datatype.Table"};
   }

   public String getModuleInfo() {
      return "<p>Overview: This module takes a <i>Table</i> and creates a graph (as " +
             "defined by the JUNG package) with vertices and edges.</p><p>Acknowledgement: " +
             "This module uses functionality from the JUNG project. See " +
             "http://jung.sourceforge.net.</p>";
   }

   public String getModuleName() {
      return "Table to Graph";
   }

   public String getInputName(int index) {

      if (index == 0) {
         return "Table";
      }
      else {
         return null;
      }

   }

   public String getOutputInfo(int index) {

      if (index == 0) {
         return "A graph (as defined by the JUNG package). It initializes " +
                "vertices and edges with <i>user data</i> that specify a " +
                "default visual representation.";
      }
      else {
         return null;
      }

   }

   public String getOutputName(int index) {

      if (index == 0) {
         return "Graph";
      }
      else {
         return null;
      }

   }

   public String[] getOutputTypes() {
      return new String[] {"edu.uci.ics.jung.graph.Graph"};
   }

   /////////////////////////////////////////////////////////////////////////////
   // properties

   private int prop_edge_label_attr = 2;
   private int prop_source_attr = 0;
   private int prop_target_attr = 1;

   public int getEdgeLabelAttr() { return prop_edge_label_attr; }
   public int getSourceAttr() { return prop_source_attr; }
   public int getTargetAttr() { return prop_target_attr; }
   public void setEdgeLabelAttr(int value) { prop_edge_label_attr = value; }
   public void setSourceAttr(int value) { prop_source_attr = value; }
   public void setTargetAttr(int value) { prop_target_attr = value; }

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

   public PropertyDescription[] getPropertiesDescriptions() { return pds; }

   public CustomModuleEditor getPropertyEditor() {
      return new PropertyEditor();
   }

   /////////////////////////////////////////////////////////////////////////////
   // custom property editor

   private class PropertyEditor extends JPanel implements ActionListener,
      CustomModuleEditor {

      private Insets emptyInsets = new Insets(1, 1, 1, 1);
      private JCheckBox edgeLblCheck;
      private JTextField edgeLblField, sourceField, targetField;

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

      public void actionPerformed(ActionEvent e) {

         Object source = e.getSource();

         if (source == edgeLblCheck) {

            edgeLblField.setEditable(edgeLblCheck.isSelected());
            edgeLblField.setEnabled(edgeLblCheck.isSelected());

         }

      }

      public boolean updateModule() throws PropertyVetoException {

         int user_source = 0, user_target = 0, user_edge_label = 0;

         try {
            user_source = Integer.parseInt(sourceField.getText());
         }
         catch (NumberFormatException e) { veto("source: not an integer"); }
         if (user_source < 0) { veto("source attribute < 0"); }

         try {
            user_target = Integer.parseInt(targetField.getText());
         }
         catch (NumberFormatException e) { veto("target: not an integer"); }
         if (user_target < 0) { veto("target attribute < 0"); }

         if (edgeLblCheck.isSelected()) {

            try {
               user_edge_label = Integer.parseInt(edgeLblField.getText());
            }
            catch (NumberFormatException e) { veto("edge label: not an integer"); }
            if (user_edge_label < 0) { veto("edge label attribute < 0"); }

         }
         else {
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

      private void veto(String reason) throws PropertyVetoException {
         throw new PropertyVetoException(reason, null);
      }

   }

}
