package ncsa.d2k.modules.core.vis.widgets;

import com.sun.j3d.utils.geometry.*;
import java.util.*;
import javax.vecmath.*;
import javax.media.j3d.*;

/**
 * <code>ComputationalGeometry</code> is intended to encapsulate common
 * computational geometry calculations such as convex hulls. As such
 * methods are written, they should be added to this class so that other
 * modules can benefit from them.
 */

public class ComputationalGeometry {

   /**
    * Calculates the convex hull of a <code>Vector</code> of
    * <code>Point3d</code> objects with respect to the xz-plane; i.e.,
    * looking down from the positive y-axis. (Useful for surface plotting.)
    * Its behavior is undefined if any points have the same y coordinate,
    * so take this into consideration before calling this method.
    *
    * @param all        the <code>Point3d</code> objects to be considered
    * @param inHull     the points in the hull (returned by reference)
    * @param notInHull  the points not in the hull (returned by reference)
    */
   public static void convexHullXZ
   (Vector all, Vector inHull, Vector notInHull) {

      inHull.clear(); notInHull.clear();

      /* the first step in Jarvis' March is to find the "lowest" point,  */
      /* which in this case is that point with the largest z coordinate. */
      /* this point is always in the convex hull, so we add it.          */

      Point3d lowest = (Point3d)all.get(0); // start with any point
      Vector remaining = notInHull;

      Point3d candidate;
      for (int i = 1; i < all.size(); i++) {

         candidate = (Point3d)all.get(i);
         if (candidate.z > lowest.z || // go "left" on a tie
            (candidate.z == lowest.z && candidate.x < lowest.x)) {
            remaining.add(lowest);
            lowest = candidate;
         }
         else
            remaining.add(candidate);

      }

      inHull.add(lowest);

      /* the next step is to find the point with the least polar angle     */
      /* with respect to the lowest point. this point is also in the hull. */

      Point3d next = (Point3d)notInHull.get(0);
      double leastAngle = polarAngleXZ(lowest, next), candidateAngle;

      int point_index = 0;
      for (int i = 1; i < notInHull.size(); i++) {

         candidate = (Point3d)notInHull.get(i);
         candidateAngle = polarAngleXZ(lowest, candidate);

         if (candidateAngle < leastAngle ||
            (candidateAngle == leastAngle &&
               lowest.distance(candidate) < lowest.distance(next))) {

            leastAngle = candidateAngle;
            next = candidate;
            point_index = i;

         }

      }

      inHull.add(notInHull.remove(point_index));

      /* we add the lowest point to the list of points not in the hull. */
      /* it really _is_ in the hull, but now we'll know that we're done */
      /* when we encounter it again in that list.                       */

      notInHull.add(lowest);

      /* ...and recurse. we require the polar angle between points on the */
      /* convex hull to be strictly increasing, which allows us to do     */
      /* this in one pass.                                                */

      Point3d current = next;
      double bestAngle, bestDistance;

      while (true) {

         // set variables...
         point_index = 0;
         bestAngle = 10; // > 2pi
         bestDistance = Double.MAX_VALUE;

         // ...and iterate over all points not in the hull, looking for
         // that point which has the least polar angle with respect to
         // the last point added to the hull (strictly increasing).
         for (int i = 0; i < notInHull.size(); i++) {

            candidate = (Point3d)notInHull.get(i);
            candidateAngle = polarAngleXZ(current, candidate);

            if ((candidateAngle >= leastAngle) &&
                (candidateAngle < bestAngle ||
                 candidateAngle == bestAngle &&
                 current.distance(candidate) < bestDistance)) {

               bestAngle = candidateAngle;
               bestDistance = current.distance(candidate);
               next = candidate;
               point_index = i;

            }

         }

         leastAngle = polarAngleXZ(current, next);

         inHull.add(notInHull.remove(point_index));

         if (next == lowest || notInHull.size() == 0)
            break;
         else
            current = next;

      }

   }

   /**
    * Calculates the polar angle (in radians) of one <code>Point3d</code>
    * object relative to another, with respect to the xz-plane as in the
    * <code>convexHullXZ</code> method.
    *
    * @param p          the reference point
    * @param q          the point whose polar angle relative to the
    *                   reference point is to be determined
    * @return           the polar angle in question
    */
   public static double polarAngleXZ(Point3d p, Point3d q) {

      double dx = q.x - p.x,
             dy = p.z - q.z; // really -dz, but this is the intuition

      if (dx == 0)                       // accounting for the
         if (dy > 0)                     // special cases in the
            return StrictMath.PI / 2;    // atan2 function
         else
            return StrictMath.PI * 1.5;
      if (dy == 0)
         if (dx > 0)
            return 0;
         else
            return StrictMath.PI;

      double angle = StrictMath.atan2(dy, dx);

      if (angle < 0)
         angle += (StrictMath.PI * 2);

      return angle;

   }

   public static IndexedTriangleArray naiveSingleTriangulationXZ
   (Vector outer, Vector inner) {

      int[] stripCounts = new int[2], two = {2};

      Point3d[] coords = new Point3d[outer.size() + inner.size()];
      HashMap Y = new HashMap();

      GeometryInfo G = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
      stripCounts[0] = outer.size();
      stripCounts[1] = inner.size();
      G.setStripCounts(stripCounts);
      G.setContourCounts(two);

      Point3d p = new Point3d(), n; // must initialize p for much later
      int offset = outer.size();
      for (int i = 0; i < offset; i++) {
         p = (Point3d)outer.get(i);
         n = new Point3d(p.x, 0, p.z);
         Y.put(n, p);
         coords[i] = n;
      }
      for (int i = offset; i < coords.length; i++) {
         p = (Point3d)inner.get(i - offset);
         n = new Point3d(p.x, 0, p.z);
         Y.put(n, p);
         coords[i] = n;
      }

      G.setCoordinates(coords);

      Triangulator T = new Triangulator();
      T.triangulate(G);

      G.recomputeIndices();

      IndexedTriangleArray pre =
         (IndexedTriangleArray)G.getIndexedGeometryArray();

      IndexedTriangleArray post =
         new IndexedTriangleArray(pre.getVertexCount(),
                                  pre.getVertexFormat(),
                                  pre.getIndexCount());

      for (int i = 0; i < post.getVertexCount(); i++) {
         pre.getCoordinate(i, p);
         post.setCoordinate(i, (Point3d)Y.get(p));
      }

      return post;

   }

}
