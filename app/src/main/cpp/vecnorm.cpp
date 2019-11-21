//
// Academic License - for use in teaching, academic research, and meeting
// course requirements at degree granting institutions only.  Not for
// government, commercial, or other organizational use.
// File: vecnorm.cpp
//
// MATLAB Coder version            : 4.2
// C/C++ source code generated on  : 14-Nov-2019 10:35:07
//

// Include Files
#include <cmath>
#include "DCM.h"
#include "vecnorm.h"

// Function Definitions

//
// Arguments    : const double x[3]
// Return Type  : double
//
double vecnorm(const double x[3])
{
  double y;
  double scale;
  double absxk;
  double t;
  scale = 3.3121686421112381E-170;
  absxk = std::abs(x[0]);
  if (absxk > 3.3121686421112381E-170) {
    y = 1.0;
    scale = absxk;
  } else {
    t = absxk / 3.3121686421112381E-170;
    y = t * t;
  }

  absxk = std::abs(x[1]);
  if (absxk > scale) {
    t = scale / absxk;
    y = 1.0 + y * t * t;
    scale = absxk;
  } else {
    t = absxk / scale;
    y += t * t;
  }

  absxk = std::abs(x[2]);
  if (absxk > scale) {
    t = scale / absxk;
    y = 1.0 + y * t * t;
    scale = absxk;
  } else {
    t = absxk / scale;
    y += t * t;
  }

  return scale * std::sqrt(y);
}

//
// File trailer for vecnorm.cpp
//
// [EOF]
//
