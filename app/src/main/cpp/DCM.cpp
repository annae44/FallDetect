//
// Academic License - for use in teaching, academic research, and meeting
// course requirements at degree granting institutions only.  Not for
// government, commercial, or other organizational use.
// File: DCM.cpp
//
// MATLAB Coder version            : 4.2
// C/C++ source code generated on  : 14-Nov-2019 10:35:07
//

// Include Files
#include <cmath>
#include "DCM.h"
#include "DCM_emxutil.cpp"
#include "vecnorm.cpp"

// Function Definitions

//
// Arguments    : const emxArray_real_T *th_acc_stat
//                const emxArray_real_T *th_acc_cs
//                emxArray_real_T *d
// Return Type  : void
//
void DCM(const emxArray_real_T *th_acc_stat, const emxArray_real_T *th_acc_cs,
         emxArray_real_T *d)
{
  emxArray_real_T *x;
  int vlen;
  int xoffset;
  int boffset;
  int aoffset;
  double accel_stat[3];
  int k;
  double angle;
  double th_acc_stat_n[3];
  static const double v2[3] = { 0.0, 0.0, 1.0 };

  signed char I3[9];
  double r_tmp[9];
  double a;
  double th_R[9];
  int n;
  emxInit_real_T(&x, 2);

  // -----Apply direction cosine matrix transformations-----------------
  // Rotate sensor frame to thigh frame %# codegen
  vlen = th_acc_stat->size[0];
  xoffset = th_acc_stat->size[0];
  boffset = th_acc_stat->size[0];
  aoffset = x->size[0] * x->size[1];
  x->size[0] = 3;
  x->size[1] = vlen;
  emxEnsureCapacity_real_T(x, aoffset);
  for (aoffset = 0; aoffset < vlen; aoffset++) {
    x->data[3 * aoffset] = th_acc_stat->data[aoffset];
  }

  for (aoffset = 0; aoffset < xoffset; aoffset++) {
    x->data[1 + 3 * aoffset] = th_acc_stat->data[aoffset + th_acc_stat->size[0]];
  }

  for (aoffset = 0; aoffset < boffset; aoffset++) {
    x->data[2 + 3 * aoffset] = th_acc_stat->data[aoffset + (th_acc_stat->size[0]
      << 1)];
  }

  vlen = x->size[1];
  if (x->size[1] == 0) {
    accel_stat[0] = 0.0;
    accel_stat[1] = 0.0;
    accel_stat[2] = 0.0;
  } else {
    accel_stat[0] = x->data[0];
    accel_stat[1] = x->data[1];
    accel_stat[2] = x->data[2];
    for (k = 2; k <= vlen; k++) {
      xoffset = (k - 1) * 3;
      accel_stat[0] += x->data[xoffset];
      accel_stat[1] += x->data[xoffset + 1];
      accel_stat[2] += x->data[xoffset + 2];
    }
  }

  vlen = x->size[1];
  accel_stat[0] /= static_cast<double>(vlen);
  accel_stat[1] /= static_cast<double>(vlen);
  accel_stat[2] /= static_cast<double>(vlen);
  angle = vecnorm(accel_stat);
  th_acc_stat_n[0] = accel_stat[0] / angle;
  th_acc_stat_n[1] = accel_stat[1] / angle;
  th_acc_stat_n[2] = accel_stat[2] / angle;

  // Reed Gurchiek, 2017
  //    getrot finds the rotation operator of type 'type' which takes v
  //    measured in frame 1 (v1) and expresses it in frame 2 (v2) if v2 is a
  //    3-dimensional vector.  Otherwise, it constructs an angle-axis
  //    rotator where v1 is the axis and v2 is the angle if v2 is
  //    1-dimensional.  In this case, consider the axis (in frame 1) and angle
  //    that one would use to rotate frame 1 to align with frame 2
  //
  // -----------------------------INPUTS---------------------------------------
  //
  //    v1, v2:
  //        vectors 1 and 2. 3xn matrix of column vectors. v1 is v measured in
  //        frame 1 and v2 is v measured in frame 2.
  //        OR
  //        rotation axis (v1: 3xn matrix of column vectors with unit norm) and
  //        rotation angle (v2: 1xn array of rotation angles)
  //
  //    type:
  //        string specifying type of rotation operator.  Either 'dcm' for
  //        direction cosine matrix or 'q' for quaternion.
  //
  // ----------------------------OUTPUTS---------------------------------------
  //
  //    r:
  //        rotation operator which takes v1 to v2 of type 'type' or described
  //        by the axis-angle combo v1 & v2.
  //
  // --------------------------------------------------------------------------
  //  getrot
  // verify proper inputs
  // if v2 is a vector
  // get axis of rotation
  accel_stat[0] = 0.0 * th_acc_stat_n[2] - th_acc_stat_n[1];
  accel_stat[1] = th_acc_stat_n[0] - 0.0 * th_acc_stat_n[2];
  accel_stat[2] = 0.0 * th_acc_stat_n[1] - 0.0 * th_acc_stat_n[0];
  angle = vecnorm(accel_stat);

  // get angle
  accel_stat[0] /= angle;
  accel_stat[1] /= angle;
  accel_stat[2] /= angle;
  angle = std::acos(((0.0 * th_acc_stat_n[0] + 0.0 * th_acc_stat_n[1]) +
                     th_acc_stat_n[2]) / (vecnorm(v2) * vecnorm(th_acc_stat_n)));

  // if v2 is 1D array of angles
  // if quaternion
  // construct dcm (euler formula: R(n,a) = I - s(a)*[nx] + (1-c(a))*[nx]^2)
  for (aoffset = 0; aoffset < 9; aoffset++) {
    I3[aoffset] = 0;
  }

  I3[0] = 1;
  I3[4] = 1;
  I3[8] = 1;


  // Reed Gurchiek, 2017
  //    skew takes a 3xn matrix of column vectors and returns a 3x3xn skew
  //    symmetric matrix for each column vector in V such that Vx(3,3,i)*p =
  //    cross(V(:,i),p).
  //
  // ---------------------------------INPUTS-----------------------------------
  //
  //    V:
  //        3xn matrix of column vectors.
  //
  // --------------------------------OUTPUTS-----------------------------------
  //
  //    Vx:
  //        3x3xn skew symmetric matrices.
  //
  // --------------------------------------------------------------------------
  //  skew
  // verify proper inputs
  // for each vector
  // get skew
  r_tmp[0] = 0.0;
  r_tmp[3] = -accel_stat[2];
  r_tmp[6] = accel_stat[1];
  r_tmp[1] = accel_stat[2];
  r_tmp[4] = 0.0;
  r_tmp[7] = -accel_stat[0];
  r_tmp[2] = -accel_stat[1];
  r_tmp[5] = accel_stat[0];
  r_tmp[8] = 0.0;
  a = std::sin(angle);
  angle = std::cos(angle);
  for (aoffset = 0; aoffset < 3; aoffset++) {
    for (vlen = 0; vlen < 3; vlen++) {
      xoffset = aoffset + 3 * vlen;
      th_R[xoffset] = (static_cast<double>(I3[xoffset]) - a * r_tmp[xoffset]) +
        (((1.0 - angle) * r_tmp[aoffset] * r_tmp[3 * vlen] + (1.0 - angle) *
          r_tmp[aoffset + 3] * r_tmp[1 + 3 * vlen]) + (1.0 - angle) *
         r_tmp[aoffset + 6] * r_tmp[2 + 3 * vlen]);
    }
  }

  vlen = th_acc_cs->size[0];
  xoffset = th_acc_cs->size[0];
  boffset = th_acc_cs->size[0];
  aoffset = x->size[0] * x->size[1];
  x->size[0] = 3;
  x->size[1] = vlen;
  emxEnsureCapacity_real_T(x, aoffset);
  for (aoffset = 0; aoffset < vlen; aoffset++) {
    x->data[3 * aoffset] = th_acc_cs->data[aoffset];
  }

  for (aoffset = 0; aoffset < xoffset; aoffset++) {
    x->data[1 + 3 * aoffset] = th_acc_cs->data[aoffset + th_acc_cs->size[0]];
  }

  for (aoffset = 0; aoffset < boffset; aoffset++) {
    x->data[2 + 3 * aoffset] = th_acc_cs->data[aoffset + (th_acc_cs->size[0] <<
      1)];
  }

  n = x->size[1];
  aoffset = d->size[0] * d->size[1];
  d->size[0] = 3;
  d->size[1] = x->size[1];
  emxEnsureCapacity_real_T(d, aoffset);
  for (vlen = 0; vlen < n; vlen++) {
    xoffset = vlen * 3;
    boffset = vlen * 3;
    d->data[xoffset] = 0.0;
    d->data[xoffset + 1] = 0.0;
    d->data[xoffset + 2] = 0.0;
    for (k = 0; k < 3; k++) {
      aoffset = k * 3;
      angle = x->data[boffset + k];
      d->data[xoffset] += angle * th_R[aoffset];
      d->data[xoffset + 1] += angle * th_R[aoffset + 1];
      d->data[xoffset + 2] += angle * th_R[aoffset + 2];
    }
  }

  emxFree_real_T(&x);
}

//
// File trailer for DCM.cpp
//
// [EOF]
//
