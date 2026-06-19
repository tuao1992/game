import React from 'react';
import { Tagline } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 32, display: 'flex', flexDirection: 'column', gap: 16, alignItems: 'center', fontSize: 26 }}>
    {children}
  </div>
);

export const Slogans = () => (
  <Stage>
    <Tagline variant="primary" />
    <Tagline variant="secondary" />
  </Stage>
);

export const Strong = () => (
  <Stage>
    <Tagline variant="primary" strong />
  </Stage>
);
