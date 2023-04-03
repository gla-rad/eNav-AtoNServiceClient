/*
 * Copyright (c) 2022 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonServiceClient.utils;

import _int.iala_aism.s125.gml._0_0.S125AidsToNavigationType;
import _int.iho.s100.gml.base._5_0.CurveProperty;
import _int.iho.s100.gml.base._5_0.PointProperty;
import _int.iho.s100.gml.base._5_0.S100SpatialAttributeType;
import _int.iho.s100.gml.base._5_0.SurfaceProperty;
import _net.opengis.gml.profiles.*;
import jakarta.xml.bind.JAXBElement;
import org.grad.eNav.s125.utils.S125Utils;
import org.locationtech.jts.geom.*;

import java.util.*;

/**
 * The type Geometry S-125 Point/Curve/Surface Converter Class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class GeometryS125Converter {

    /**
     * Convert an S-125 Aids to Navigation entry to the JTS Geometry.
     *
     * @param s125AidsToNavigationType the S-125 Aids to Navigation
     * @return the respective JTS geometry object
     */
    public Geometry convertToGeometry(S125AidsToNavigationType s125AidsToNavigationType) {
        return Optional.ofNullable(s125AidsToNavigationType)
                .map(S125Utils::getS125AidsToNavigationTypeGeometriesList)
                .map(this::s125PointCurveSurfaceToGeometry)
                .orElse(null);
    }

    /**
     * Translates the generic point/curve/surface property of the S-125
     * feature type into a JTS geometry (most likely a geometry collection)
     * that can be understood and handled by the services.
     *
     * @param s100SpatialAttributeTypes the S-100 point/curve/surface property
     * @return the respective geometry
     */
    protected Geometry s125PointCurveSurfaceToGeometry(List<S100SpatialAttributeType> s100SpatialAttributeTypes) {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return s100SpatialAttributeTypes.stream()
                .map(pty -> {
                    // Map based on the type of the populated geometry
                    if(pty instanceof PointProperty) {
                        return Optional.of(pty)
                                .map(PointProperty.class::cast)
                                .map(PointProperty::getPoint)
                                .map(PointType::getPos)
                                .map(pos -> new Coordinate(pos.getValue()[0], pos.getValue()[1]))
                                .map(geometryFactory::createPoint)
                                .map(Geometry.class::cast)
                                .orElse(geometryFactory.createEmpty(0));
                    } else if(pty instanceof CurveProperty) {
                        return geometryFactory.createGeometryCollection(Optional.of(pty)
                                .map(CurveProperty.class::cast)
                                .map(CurveProperty::getCurve)
                                .map(CurveType::getSegments)
                                .map(Segments::getAbstractCurveSegments)
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(JAXBElement::getValue)
                                .filter(LineStringSegmentType.class::isInstance)
                                .map(LineStringSegmentType.class::cast)
                                .map(LineStringSegmentType::getPosList)
                                .map(this::gmlPosListToCoordinates)
                                .map(coords -> coords.length == 1? geometryFactory.createPoint(coords[0]) : geometryFactory.createLineString(coords))
                                .toList()
                                .toArray(Geometry[]::new));
                    } else if(pty instanceof SurfaceProperty) {
                        return geometryFactory.createGeometryCollection(Optional.of(pty)
                                .map(SurfaceProperty.class::cast)
                                .map(SurfaceProperty::getSurface)
                                .map(SurfaceType::getPatches)
                                .map(Patches::getAbstractSurfacePatches)
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(JAXBElement::getValue)
                                .filter(PolygonPatchType.class::isInstance)
                                .map(PolygonPatchType.class::cast)
                                .map(PolygonPatchType::getExterior)
                                .map(AbstractRingPropertyType::getAbstractRing)
                                .map(JAXBElement::getValue)
                                .filter(LinearRingType.class::isInstance)
                                .map(LinearRingType.class::cast)
                                .map(LinearRingType::getPosList)
                                .map(this::gmlPosListToCoordinates)
                                .map(coords -> coords.length == 1? geometryFactory.createPoint(coords[0]) : geometryFactory.createPolygon(coords))
                                .toList()
                                .toArray(Geometry[]::new));
                    }
                    return null;
                })
                .reduce(geometryFactory.createEmpty(-1), (un, el) -> un == null || un.isEmpty() ? el : un.union(el));
    }

    /**
     * Translates the generic JTS geometry property of the local Aids to
     * Navigation objects to a generic S-125 point/curve/surface geometry
     * that can be understood and handled by the S-125 services.
     *
     * @param geometry      The JTS geometry object
     * @return the S-125 point/curve/surface geometry
     */
    protected List<S100SpatialAttributeType> geometryToS125PointCurveSurfaceGeometry(Geometry geometry) {
        // Return the populated property
        return populatePointCurveSurfaceToGeometry(geometry, new ArrayList<>());
    }

    /**
     * A iterative helper function that examines the provided geometry and
     * dives deeper into collection to pick up the basic JTS geometry types
     * such as the points, lines and polygons.
     *
     * @param geometry                      The geometry to be examined
     * @param s100SpatialAttributeTypes     The S-125 geometry object to be populated
     */
    protected List<S100SpatialAttributeType> populatePointCurveSurfaceToGeometry(Geometry geometry, List<S100SpatialAttributeType> s100SpatialAttributeTypes) {
        // Create an OpenGIS GML factory
        ObjectFactory opengisGMLFactory = new ObjectFactory();
        s100SpatialAttributeTypes = s100SpatialAttributeTypes == null ? new ArrayList() : s100SpatialAttributeTypes;

        if(geometry instanceof Puntal) {
            // Initialise the point property if not already initialised
            PointProperty pointProperty = this.initPointProperty();

            // And append the point
            pointProperty.getPoint().setPos(
                    this.generatePointPropertyPosition(coordinatesToGmlPosList(geometry.getCoordinates()).getValue())
            );
            s100SpatialAttributeTypes.add(pointProperty);
        } else if(geometry instanceof Lineal) {
            // Initialise the curve property if not already initialised
            CurveProperty curveProperty = this.initialiseCurveProperty();

            // And append the line string
            curveProperty.getCurve().getSegments().getAbstractCurveSegments().add(
                    opengisGMLFactory.createLineStringSegment(generateCurvePropertySegment(coordinatesToGmlPosList(geometry.getCoordinates()).getValue()))
            );
            s100SpatialAttributeTypes.add(curveProperty);
        } else if(geometry instanceof Polygonal) {
            // Initialise the curve property if not already initialised
            SurfaceProperty surfaceProperty = this.initialiseSurfaceProperty();

            // And append the surface patch
            surfaceProperty.getSurface().getPatches().getAbstractSurfacePatches().add(
                    opengisGMLFactory.createPolygonPatch(generateSurfacePropertyPatch(coordinatesToGmlPosList(geometry.getCoordinates()).getValue()))
            );
            s100SpatialAttributeTypes.add(surfaceProperty);
        } else if(geometry instanceof GeometryCollection && geometry.getNumGeometries() > 0) {
            for(int i=0; i < geometry.getNumGeometries(); i++) {
                this.populatePointCurveSurfaceToGeometry(geometry.getGeometryN(i), s100SpatialAttributeTypes);
            }
        }

        // And return the property
        return s100SpatialAttributeTypes;
    }

    /**
     * Populates and return an S-125 surface property based on the provided
     * surface geometry coordinates.
     *
     * @param coords    The coordinates of the element to be generated
     * @return The populated point property
     */
    protected PolygonPatchType generateSurfacePropertyPatch(Double[] coords) {
        // Create an OpenGIS GML factory
        ObjectFactory opengisGMLFactory = new ObjectFactory();

        // Generate the elements
        PolygonPatchType polygonPatchType = new PolygonPatchType();
        AbstractRingPropertyType abstractRingPropertyType = new AbstractRingPropertyType();
        LinearRingType linearRingType = new LinearRingType();
        PosList posList = new PosList();

        // Populate with the geometry data
        posList.setValue(coords);

        // Populate the elements
        linearRingType.setPosList(posList);
        abstractRingPropertyType.setAbstractRing(opengisGMLFactory.createLinearRing(linearRingType));
        polygonPatchType.setExterior(abstractRingPropertyType);

        // And return the output
        return polygonPatchType;
    }

    /**
     * Populates and return an S-125 curve property based on the provided line
     * segment geometry coordinates.
     *
     * @param coords    The coordinates of the element to be generated
     * @return The populated point property
     */
    protected LineStringSegmentType generateCurvePropertySegment(Double[] coords) {
        // Generate the elements
        LineStringSegmentType lineStringSegmentType = new LineStringSegmentType();
        PosList posList = new PosList();

        // Populate with the geometry data
        posList.setValue(coords);
        lineStringSegmentType.setPosList(posList);

        // And return the output
        return lineStringSegmentType;
    }

    /**
     * Populates and return an S-125 point property based on the provided point
     * geometry coordinates.
     *
     * @param coords    The coordinates of the element to be generated
     * @return The populated point property
     */
    protected Pos generatePointPropertyPosition(Double[] coords) {
        // Generate the elements
        Pos pos = new Pos();

        // Populate with the geometry data
        pos.setValue(coords);

        // And return the output
        return pos;
    }

    /**
     * Initialise the S-125 Surface Property object
     *
     * @return the initialised S-125 Surface Property object
     */
    protected SurfaceProperty initialiseSurfaceProperty() {
        // Create an OpenGIS GML factory
        ObjectFactory opengisGMLFactory = new ObjectFactory();

        // Generate the elements
        SurfaceProperty surfaceProperty = new SurfaceProperty();
        _int.iho.s100.gml.base._5_0.SurfaceType surfaceType = new _int.iho.s100.gml.base._5_0.SurfaceType();
        Patches patches = new Patches();

        // Populate the elements
        surfaceType.setPatches(patches);
        surfaceProperty.setSurface(surfaceType);

        // And return the output
        return surfaceProperty;
    }

    /**
     * Initialise the S-125 Curve Property object
     *
     * @return the initialised S-125 Curve Property object
     */
    protected CurveProperty initialiseCurveProperty() {
        // Generate the elements
        CurveProperty curveProperty = new CurveProperty();
        _int.iho.s100.gml.base._5_0.CurveType curveType = new _int.iho.s100.gml.base._5_0.CurveType();
        Segments segments = new Segments();

        // Populate the elements
        curveType.setSegments(segments);
        curveProperty.setCurve(curveType);

        // And return the output
        return curveProperty;
    }

    /**
     * Initialise the S-125 Point Property object
     *
     * @return the initialised S-125 Point Property object
     */
    protected PointProperty initPointProperty() {
        // Generate the elements
        PointProperty pointProperty = new PointProperty();
        _int.iho.s100.gml.base._5_0.PointType pointType = new _int.iho.s100.gml.base._5_0.PointType();

        // Populate the elements
        pointProperty.setPoint(pointType);

        // And return the output
        return pointProperty;
    }

    /**
     * A simple utility function that splits the position list values by two
     * and generates JTS geometry coordinates by them.
     *
     * @param posList the provided position list
     * @return the respective coordinates
     */
    protected Coordinate[] gmlPosListToCoordinates(PosList posList) {
        final List<Coordinate> result = new ArrayList<>();
        for(int i=0; i<posList.getValue().length; i=i+2) {
            result.add(new Coordinate(posList.getValue()[i], posList.getValue()[i+1]));
        }
        return result.toArray(new Coordinate[]{});
    }

    /**
     * A simple utility function that receives JTS geometry coordinates and
     * constructs a position list object.
     *
     * @param coordinates the provided coordinates
     * @return the respective position list
     */
    protected PosList coordinatesToGmlPosList(Coordinate[] coordinates) {
        // Translate the coordinates to a simple list of doubles (Y, X)
        List<Double> coords = Optional.ofNullable(coordinates)
                .map(Arrays::asList)
                .orElse(Collections.emptyList())
                .stream()
                .map(c -> Arrays.asList(c.getX(), c.getY()))
                .flatMap(List::stream).toList();

        // The create the list and return
        PosList posList = new PosList();
        posList.setValue(coords.toArray(Double[]::new));
        return posList;
    }

}
