import React from 'react';
import { Gauge } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 28, display: 'flex', gap: 28, flexWrap: 'wrap', alignItems: 'center' }}>
    {children}
  </div>
);

export const PressureTest = () => (
  <Stage>
    <Gauge value={0.96} tone="success" display="HELD" label="PRESSURE" />
    <Gauge value={0.34} tone="danger" display="LEAK" label="PRESSURE" />
  </Stage>
);

export const Levels = () => (
  <Stage>
    <Gauge value={0.25} tone="blue" />
    <Gauge value={0.6} tone="amber" />
    <Gauge value={0.92} tone="success" />
  </Stage>
);

export const Small = () => (
  <Stage>
    <Gauge value={0.78} size={96} tone="blue" label="SEAL" />
  </Stage>
);
