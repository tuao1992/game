import React from 'react';
import { CementCan } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 28, display: 'flex', gap: 28, alignItems: 'flex-end', justifyContent: 'center' }}>
    {children}
  </div>
);

export const Default = () => (
  <Stage>
    <CementCan size={180} />
  </Stage>
);

export const Glow = () => (
  <Stage>
    <CementCan size={200} glow />
  </Stage>
);

export const Sizes = () => (
  <Stage>
    <CementCan size={90} />
    <CementCan size={130} />
    <CementCan size={170} />
  </Stage>
);
