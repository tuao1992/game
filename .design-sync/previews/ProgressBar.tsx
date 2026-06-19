import React from 'react';
import { ProgressBar } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 28, display: 'flex', flexDirection: 'column', gap: 22, width: 420 }}>
    {children}
  </div>
);

export const Tones = () => (
  <Stage>
    <ProgressBar value={0.62} tone="blue" label="XP to Technician" caption="62%" />
    <ProgressBar value={0.9} tone="success" label="Hold to join" caption="Strong" />
    <ProgressBar value={0.35} tone="amber" label="Cement open time" caption="Hurry" />
  </Stage>
);

export const CareerProgress = () => (
  <Stage>
    <ProgressBar value={24 / 50} tone="blue" label="Career" caption="24 / 50 levels" />
  </Stage>
);

export const Levels = () => (
  <Stage>
    <ProgressBar value={0.1} tone="blue" />
    <ProgressBar value={0.5} tone="blue" />
    <ProgressBar value={1} tone="success" />
  </Stage>
);
