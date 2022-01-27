* **version:** 0.9.3
* **status:** released (first release version: 0.8.5)
* **comments:** We have been using this library for robot development since December 2020, and
believe it is ready for general use.

# a05annexUtil
Utility classes for use in First FTC/FRC robot code, but that are not dependent on either
First FTC or First FRC infrastructure. We built this library as we realized that there 
is a lot of common code (functionality) that we use both inside and outside the
robot-specific code - for example, in autonomous path planning, reading/writing JSON
data files, etc. We wanted to package that code so that it could be
used regardless of the FTC/FRC base platform libraries and/or hardware.

## a05annexUtil Library Contents

* **`geo2d`** - some 2d geometric classes
  * **`KochanekBartelsSpline`** - a spline formulation continuous in position, velocity, and
    acceleration.
  * **`Vector2d`** - a 2d vector used in the spline code.
  * **`Plane2d`** - a 2d representation of a plane that we can use for collision testing between the robot
    and the field boundary or field elements during path planning.
* **`geo3d`** - some 3d geometric classes
  * **`Line3d`** - an explicit parametric 3d line.
  * **`PackageContents`** - the constants and test for *close enough* to be considered
    zero (`0.0`). Used in matrix singularity and zero length vector normalization
  * checks.
  * **`Plane3d`** - and implicit 3d plane.
  * **`Point3d`** - a 3d point.
  * **`SingularMatrixException`** - An exception thrown when a singular matrix is inverted.
  * **`Vector3d`** - a 3d vector.
  * **`Xfm4x4d`** - a 3d transformation that can be applied to points and vectors.
  * **`ZeroLengthVectorException`** - And exception thrown when a zero-length vector is normalized.
* **`AngleD`** - a class that maintains angles in radians with convenience methods for degree input/output.
* **`AngleConstantD`** - a class that maintains constant angles.
* **`AngleTYpe`** - The angle unit specification..
* **`JsonSupport`** - a class with helper functions to aid in reading/writing JSON files.
* **`Utl`** - a class which extends the java `Math` class with variable argument `min()`, `max()`,
  `length()`, and `clip()` functions.

## Including a05annexUtil in your build.gradle

We wanted to package `a05annexUtil` so it was easy for us to use in future years, and
easy for you to use in your FRC projects. We found this was not as easy as we had
hoped as publishing artifacts to Maven repositories is non-trivial, but we now have
it all working.

There are a couple paths for inclusion.

### The Best Method

Simply add it to the dependencies section of your `gradle.build` file as:
```
dependencies {
    implementation 'org.a05annex:a05annexUtil:0.9.3'
     .
     .
     .
}
```

Also add a dependency for `testCompile` if you need it for testing.

### Download the .jar and Make it Part of Your Project

The next most simple way to use **a05annexUtil**, following the advice from this
[chiefdelphi post](https://www.chiefdelphi.com/t/adding-my-teams-library-as-a-vendor-library/339626)
and advises you:
* create a `libs` folder in your robot project
* copy the `a05annxUtil-0.9.3.jar` file from the github 0.9.3 release into that `libs` folder
* in the dependencies section of the `build.gradle` file add the line:  
  `implementation fileTree(dir: 'libs', include: ['*.jar'])`
* add the `libs/a05annxUtil-0.9.3.jar` to **git** so it is saved as part of your project.

The disadvantage of this method is that you must manually download the library and
put it in your project, you also need to check for version updates.

## Release Notes

* version 0.9.3 - ??-Jan-2022 - Robust AngleD and AngleConstantD implementation.
* version 0.9.1 - 11-Nov-2021 - 3d vector geometry addition.
* version 0.8.7 - 25-Apr-2021 - Some convenience fixes:
  * KochanekBartelsSpline:
    * `load(...)` and `save(...)` now return `true`-`false` to indicate success or
      failure of the load-save;
    * added a speed multiplier, allowing the speed of path traversal to be uniformly scaled up or down;
    * added time control for individual points on the path allowing path segment speeds to te
      finely controlled relative to other path segments;
    * adjusted the operation of `controlPointDelete()` and `controlPointInsert()` for minimal disruption of
      path geometry and timing when they are used;
    * more complete unit testing for the spline operators.
  * Added the `Utl.clip(...)` function and with testing to support subsystem tuning branches.
* version 0.8.6 - 14-Feb-2021 - Fixed issue #1, load problems for an empty (no control points) path.
* version 0.8.5 - 26-Dec-2020 - The first real release. Things were mostly tested and documented.
* version 0.8.0 - 18-Nov-2020 - We were just trying to figure out the release process - please ignore this one

    

