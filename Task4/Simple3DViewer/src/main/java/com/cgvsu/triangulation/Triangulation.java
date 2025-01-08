package com.cgvsu.triangulation;

import com.cgvsu.model.Polygon;
import com.cgvsu.model.Model;

import java.util.ArrayList;

public class Triangulation {

    public static void triangulate(Model model) {
        ArrayList<Polygon> triangulatedPolygons = new ArrayList<>();

        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();

            int verticesCount = vertexIndices.size();

            if (verticesCount < 3) {
                continue; // Пропускаем полигоны с менее чем 3 вершинами
            }

            for (int i = 1; i < verticesCount - 1; i++) {
                Polygon triangle = new Polygon();

                ArrayList<Integer> triangleVertexIndices = new ArrayList<>();
                triangleVertexIndices.add(vertexIndices.get(0));
                triangleVertexIndices.add(vertexIndices.get(i));
                triangleVertexIndices.add(vertexIndices.get(i + 1));
                triangle.setVertexIndices(triangleVertexIndices);

                if (!textureVertexIndices.isEmpty()) {
                    ArrayList<Integer> triangleTextureVertexIndices = new ArrayList<>();
                    triangleTextureVertexIndices.add(textureVertexIndices.get(0));
                    triangleTextureVertexIndices.add(textureVertexIndices.get(i));
                    triangleTextureVertexIndices.add(textureVertexIndices.get(i + 1));
                    triangle.setTextureVertexIndices(triangleTextureVertexIndices);
                }

                if (!normalIndices.isEmpty()) {
                    ArrayList<Integer> triangleNormalIndices = new ArrayList<>();
                    triangleNormalIndices.add(normalIndices.get(0));
                    triangleNormalIndices.add(normalIndices.get(i));
                    triangleNormalIndices.add(normalIndices.get(i + 1));
                    triangle.setNormalIndices(triangleNormalIndices);
                }

                triangulatedPolygons.add(triangle);
            }
        }

        model.polygons = triangulatedPolygons;
    }
}