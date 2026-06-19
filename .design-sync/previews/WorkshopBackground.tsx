import React from 'react';
import { WorkshopBackground, Logo, Tagline, Button } from '@weldrite/design-system';

export const MenuStage = () => (
  <WorkshopBackground minHeight={360} style={{ borderRadius: 16 }}>
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 20 }}>
      <Logo size={64} />
      <Tagline variant="primary" />
      <Button variant="primary" size="lg">
        PLAY
      </Button>
    </div>
  </WorkshopBackground>
);

export const KitchenChapter = () => (
  <WorkshopBackground accent="var(--wr-env-kitchen)" minHeight={300} style={{ borderRadius: 16 }}>
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 12 }}>
      <div style={{ fontFamily: 'var(--wr-font)', fontWeight: 800, fontSize: 26, color: '#fff' }}>
        Kitchen
      </div>
      <Tagline variant="secondary" strong />
    </div>
  </WorkshopBackground>
);
