import java.util.*;

public class ClosestPair
{
    public static class Pair
    {
        public Point point1;
        public Point point2;
        public double distance;

        public Pair(Point point1, Point point2)
        {
            this.point1 = point1;
            this.point2 = point2;
            calcDistance();
        }

        public void update(Point point1, Point point2, double distance)
        {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = distance;
        }

        public void calcDistance()
        {  this.distance = distance(point1, point2);  }

        public String toString()
        {  return "p ( " + point1 + ", " + point2 + " ) = " + distance;  }
    }

    public static double distance(Point point1, Point point2)
    {
        double xDist = point2.x - point1.x;
        double yDist = point2.y - point1.y;
        return Math.hypot(xDist, yDist);
    }

    public static Pair bruteForce(List<? extends Point> points)
    {
        int numPoints = points.size();
        if (numPoints < 2)
            return null;
        Pair pair = new Pair(points.get(0), points.get(1));
        if (numPoints > 2)
        {
            for (int i = 0; i < numPoints - 1; i++)
            {
                Point point1 = points.get(i);
                for (int j = i + 1; j < numPoints; j++)
                {
                    Point point2 = points.get(j);
                    double distance = distance(point1, point2);
                    if (distance < pair.distance)
                        pair.update(point1, point2, distance);
                }
            }
        }
        return pair;
    }

    public static void sortByX(List<? extends Point> points)
    {
        Collections.sort(points, new Comparator<Point>() {
                    public int compare(Point point1, Point point2)
                    {
                        if (point1.x < point2.x)
                            return -1;
                        if (point1.x > point2.x)
                            return 1;
                        return 0;
                    }
                }
        );
    }

    public static void sortByY(List<? extends Point> points)
    {
        Collections.sort(points, new Comparator<Point>() {
                    public int compare(Point point1, Point point2)
                    {
                        if (point1.y < point2.y)
                            return -1;
                        if (point1.y > point2.y)
                            return 1;
                        return 0;
                    }
                }
        );
    }

    public static Pair divideAndConquer(List<? extends Point> points)
    {
        List<Point> pointsSortedByX = new ArrayList<Point>(points);
        sortByX(pointsSortedByX);
        List<Point> pointsSortedByY = new ArrayList<Point>(points);
        sortByY(pointsSortedByY);
        return divideAndConquer(pointsSortedByX, pointsSortedByY);
    }

    private static Pair divideAndConquer(List<? extends Point> pointsSortedByX, List<? extends Point> pointsSortedByY)
    {
        int numPoints = pointsSortedByX.size();
        if (numPoints <= 6)
            return bruteForce(pointsSortedByX);

        int dividingIndex = 2;
        List<? extends Point> leftOfCenter = pointsSortedByX.subList(0, dividingIndex);
        List<? extends Point> rightOfCenter = pointsSortedByX.subList(dividingIndex, numPoints);

        List<Point> tempList = new ArrayList<Point>(leftOfCenter);
        sortByY(tempList);
        Pair closestPairLeft = divideAndConquer(leftOfCenter, tempList);

        tempList.clear();
        tempList.addAll(rightOfCenter);
        sortByY(tempList);
        Pair closestPairRight = divideAndConquer(rightOfCenter, tempList);

        if (closestPairRight.distance < closestPairLeft.distance)
            closestPairLeft = closestPairRight;

        tempList.clear();
        double shortestDistance = closestPairLeft.distance;
        double centerX = rightOfCenter.get(0).x;
        for (Point point : pointsSortedByY)
            if (Math.abs(centerX - point.x) < shortestDistance)
                tempList.add(point);

        for (int i = 0; i < tempList.size() - 1; i++)
        {
            Point point1 = tempList.get(i);
            for (int j = i + 1; j < tempList.size(); j++)
            {
                Point point2 = tempList.get(j);
                if ((point2.y - point1.y) >= shortestDistance)
                    break;
                double distance = distance(point1, point2);
                if (distance < closestPairLeft.distance)
                {
                    closestPairLeft.update(point1, point2, distance);
                    shortestDistance = distance;
                }
            }
        }
        return closestPairLeft;
    }

    public static void main(String[] args)
    {
        Random rand = new Random();
        for(String arg : args){
            int numPoints = Integer.parseInt(arg);
            List<Point> points = new ArrayList<Point>();
            for (int i = 0; i < numPoints; i++)
                points.add(new Point(rand.nextDouble(), rand.nextDouble()));
            System.out.println("Generated " + numPoints + " random points");
            long startTime = System.currentTimeMillis();
            Pair bruteForceClosestPair = bruteForce(points);
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Brute force (" + elapsedTime + " ms): " + bruteForceClosestPair);
            startTime = System.currentTimeMillis();
            Pair dqClosestPair = divideAndConquer(points);
            elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Divide and conquer (" + elapsedTime + " ms): " + dqClosestPair);
            System.out.println("\n---------------------------------------------------------------------------------\n");
        }
    }
}
