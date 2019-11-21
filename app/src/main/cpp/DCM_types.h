//
// Academic License - for use in teaching, academic research, and meeting
// course requirements at degree granting institutions only.  Not for
// government, commercial, or other organizational use.
// File: DCM_types.h
//
// MATLAB Coder version            : 4.2
// C/C++ source code generated on  : 14-Nov-2019 10:35:07
//
#ifndef DCM_TYPES_H
#define DCM_TYPES_H

// Include Files
#include "rtwtypes.h"

// Type Definitions
struct emxArray_real_T
{
  double *data;
  int *size;
  int allocatedSize;
  int numDimensions;
  boolean_T canFreeData;
};

#endif

//
// File trailer for DCM_types.h
//
// [EOF]
//
